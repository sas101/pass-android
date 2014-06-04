package com.simroth.passwordstore.crypto;
// originally from package com.fsck.k9.crypto

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.text.ClipboardManager;
import android.widget.Toast;
import com.simroth.passwordstore.R;

import java.util.regex.Pattern;

/**
 * APG integration.
 */
public class Apg {
    static final long serialVersionUID = 0x21071235;
    public static final String NAME = "apg";

    private static final String mApgPackageName = "org.thialfihar.android.apg";
    private static final int mMinRequiredVersion = 16;

    public static final String AUTHORITY = "org.thialfihar.android.apg.provider";
    public static final Uri CONTENT_URI_SECRET_KEY_RING_BY_KEY_ID =
            Uri.parse("content://" + AUTHORITY + "/key_rings/secret/key_id/");
    public static final Uri CONTENT_URI_SECRET_KEY_RING_BY_EMAILS =
            Uri.parse("content://" + AUTHORITY + "/key_rings/secret/emails/");

    public static final Uri CONTENT_URI_PUBLIC_KEY_RING_BY_KEY_ID =
            Uri.parse("content://" + AUTHORITY + "/key_rings/public/key_id/");
    public static final Uri CONTENT_URI_PUBLIC_KEY_RING_BY_EMAILS =
            Uri.parse("content://" + AUTHORITY + "/key_rings/public/emails/");

    public static class Intent {
        public static final String DECRYPT = "org.thialfihar.android.apg.intent.DECRYPT";
        public static final String ENCRYPT = "org.thialfihar.android.apg.intent.ENCRYPT";
        public static final String DECRYPT_FILE = "org.thialfihar.android.apg.intent.DECRYPT_FILE";
        public static final String ENCRYPT_FILE = "org.thialfihar.android.apg.intent.ENCRYPT_FILE";
        public static final String DECRYPT_AND_RETURN = "org.thialfihar.android.apg.intent.DECRYPT_AND_RETURN";
        public static final String ENCRYPT_AND_RETURN = "org.thialfihar.android.apg.intent.ENCRYPT_AND_RETURN";
        public static final String SELECT_PUBLIC_KEYS = "org.thialfihar.android.apg.intent.SELECT_PUBLIC_KEYS";
        public static final String SELECT_SECRET_KEY = "org.thialfihar.android.apg.intent.SELECT_SECRET_KEY";
    }

    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_DATA = "data";
    public static final String EXTRA_ERROR = "error";
    public static final String EXTRA_DECRYPTED_MESSAGE = "decryptedMessage";
    public static final String EXTRA_ENCRYPTED_MESSAGE = "encryptedMessage";
    public static final String EXTRA_SIGNATURE = "signature";
    public static final String EXTRA_SIGNATURE_KEY_ID = "signatureKeyId";
    public static final String EXTRA_SIGNATURE_USER_ID = "signatureUserId";
    public static final String EXTRA_SIGNATURE_SUCCESS = "signatureSuccess";
    public static final String EXTRA_SIGNATURE_UNKNOWN = "signatureUnknown";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_KEY_ID = "keyId";
    public static final String EXTRA_ENCRYPTION_KEY_IDS = "encryptionKeyIds";
    public static final String EXTRA_SELECTION = "selection";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_INTENT_VERSION = "intentVersion";

    public static final String INTENT_VERSION = "1";

    // Note: The support package only allows us to use the lower 16 bits of a request code.
    public static final int DECRYPT_MESSAGE = 0x0000A001;
    public static final int ENCRYPT_MESSAGE = 0x0000A002;
    public static final int SELECT_PUBLIC_KEYS = 0x0000A003;
    public static final int SELECT_SECRET_KEY = 0x0000A004;

    public static Pattern PGP_MESSAGE =
            Pattern.compile(".*?(-----BEGIN PGP MESSAGE-----.*?-----END PGP MESSAGE-----).*",
                    Pattern.DOTALL);

    public static Pattern PGP_SIGNED_MESSAGE =
            Pattern.compile(".*?(-----BEGIN PGP SIGNED MESSAGE-----.*?-----BEGIN PGP SIGNATURE-----.*?-----END PGP SIGNATURE-----).*",
                    Pattern.DOTALL);

    public static Apg createInstance() {
        return new Apg();
    }

    /**
     * Check whether APG is installed and at a high enough version.
     *
     * @param context
     * @return whether a suitable version of APG was found
     */
    public boolean isAvailable(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(mApgPackageName, 0);
            if (pi.versionCode >= mMinRequiredVersion) {
                return true;
            } else {
                Toast.makeText(context,
                        R.string.error_apg_version_not_supported, Toast.LENGTH_SHORT).show();
            }
        } catch (NameNotFoundException e) {
            // not found
        }

        return false;
    }

    /**
     * Handle the activity results that concern us.
     *
     * @param activity
     * @param requestCode
     * @param resultCode
     * @param data
     * @return handled or not
     */
    public boolean onActivityResult(Activity activity, int requestCode, int resultCode,
                                    android.content.Intent data, PgpData pgpData) {
        switch (requestCode) {

            default:
                return false;
        }
    }

    public boolean onDecryptActivityResult(CryptoDecryptCallback callback, int requestCode,
                                           int resultCode, android.content.Intent data, PgpData pgpData) {

        switch (requestCode) {
            case Apg.DECRYPT_MESSAGE: {
                if (resultCode != Activity.RESULT_OK || data == null) {
                    break;
                }

                pgpData.setSignatureUserId(data.getStringExtra(Apg.EXTRA_SIGNATURE_USER_ID));
                pgpData.setSignatureKeyId(data.getLongExtra(Apg.EXTRA_SIGNATURE_KEY_ID, 0));
                pgpData.setSignatureSuccess(data.getBooleanExtra(Apg.EXTRA_SIGNATURE_SUCCESS, false));
                pgpData.setSignatureUnknown(data.getBooleanExtra(Apg.EXTRA_SIGNATURE_UNKNOWN, false));

                pgpData.setDecryptedData(data.getStringExtra(Apg.EXTRA_DECRYPTED_MESSAGE));
                callback.onDecryptDone(pgpData);

                break;
            }
            default: {
                return false;
            }
        }

        return true;
    }

    /**
     * Start the decrypt activity.
     *
     * @return success or failure
     */
    public boolean decrypt(Activity activity, byte[] data) {
        android.content.Intent intent = new android.content.Intent(Apg.Intent.DECRYPT_AND_RETURN);
        intent.putExtra(EXTRA_INTENT_VERSION, INTENT_VERSION);
        intent.setType("text/plain");
        if (data == null) {
            return false;
        }
        try {
            intent.putExtra(EXTRA_DATA, data);

            // workaround for APG: add something to the clipboard or else APG will crash :-/
            @SuppressWarnings("deprecation") ClipboardManager clip = (ClipboardManager) activity.getSystemService(Activity.CLIPBOARD_SERVICE);
            clip.setText("foo");

            activity.startActivityForResult(intent, Apg.DECRYPT_MESSAGE);
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.error_activity_not_found, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Get the name of the provider.
     *
     * @return provider name
     */
    public String getName() {
        return NAME;
    }

    /**
     * Test the APG installation.
     *
     * @return success or failure
     */
    public boolean test(Context context) {
        if (!isAvailable(context)) {
            return false;
        }

        try {
            // try out one content provider to check permissions
            Uri contentUri = ContentUris.withAppendedId(
                    Apg.CONTENT_URI_SECRET_KEY_RING_BY_KEY_ID,
                    12345);
            Cursor c = context.getContentResolver().query(contentUri,
                    new String[] { "user_id" },
                    null, null, null);
            if (c != null) {
                c.close();
            }
        } catch (SecurityException e) {
            // if there was a problem, then let the user know, this will not stop K9/APG from
            // working, but some features won't be available, so we can still return "true"
            Toast.makeText(context,
                    context.getResources().getString(R.string.insufficient_apg_permissions),
                    Toast.LENGTH_LONG).show();
        }

        return true;
    }
}