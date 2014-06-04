package com.simroth.passwordstore.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;
import com.simroth.passwordstore.PasswordStoreApp;
import com.simroth.passwordstore.R;
import com.simroth.passwordstore.crypto.Apg;
import com.simroth.passwordstore.util.DebugUtils;
import com.simroth.passwordstore.util.Ln;
import com.squareup.otto.Bus;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.*;

public class PasswordStoreActivity extends Activity {

    @Inject
    Bus bus;

    @InjectView(R.id.button_apg_decrypt)
    Button decryptApgButton;
    @InjectView(R.id.text_filename)
    TextView fileNameTextView;
    @InjectView(R.id.text_decrypted)
    TextView decryptedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PasswordStoreApp.getInstance().inject(this);

        setContentView(R.layout.activity_main);

        Views.inject(this);

        Ln.d("Bus: %s", bus != null ? bus.toString() : null);

        decryptApgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileNameTextView.setText("File: ");
                decryptedTextView.setText("");
                openFile();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String intentData = DebugUtils.debugIntent(data);
        Ln.d(intentData);

        switch (requestCode) {
            case Apg.DECRYPT_MESSAGE:
                if (resultCode == RESULT_OK) {
                    String decrypted = data.getStringExtra("decryptedMessage");
                    // Toast.makeText(this, decrypted, Toast.LENGTH_LONG).show();
                    decryptedTextView.setText(decrypted);
                }
                break;
            case 0x21070003:
                if (resultCode == RESULT_OK) {
                    String fileName = resolveFileName(data);
                    fileNameTextView.setText("File: " + fileName);
                    // Toast.makeText(this, fileName != null ? fileName : "fileName null", Toast.LENGTH_LONG).show();
                    if (fileName != null) {
                        decryptFile(fileName);
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("ConstantConditions")
    private String resolveFileName(Intent intent) {
        Uri uri = intent.getData();
        String filePath = null;
        if (uri != null && uri.getScheme() != null) {
            if (uri.getScheme().equals("content")) {
                Cursor c = getContentResolver().query(uri, null, null, null, null);
                if (c != null && c.moveToFirst()) {
                    int id = c.getColumnIndex(MediaStore.Images.Media.DATA);
                    if (id != -1) {
                        filePath = c.getString(id);
                    }
                }
            } else if (uri.getScheme().equals("file")) {
                filePath = uri.getPath();
            } else {
                //TODO unknown uri scheme.
            }
        }
        return filePath;
    }

    private void decryptFile(String fileName) {
        Apg apg = new Apg();
        byte[] data = new byte[0];
        try {
            data = FileUtils.readFileToByteArray(new File(fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
        apg.decrypt(PasswordStoreActivity.this, data);
    }

    private void openFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        //intent.setData(Uri.parse("file://" + filename));
        intent.setType("*/*");

        try {
            startActivityForResult(intent, 0x21070003);
        } catch (ActivityNotFoundException e) {
            // No compatible file manager was found.
            Toast.makeText(this, "R.string.noFilemanagerInstalled", Toast.LENGTH_SHORT).show();
        }
    }
}
