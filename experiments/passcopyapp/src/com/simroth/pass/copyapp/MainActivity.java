package com.simroth.pass.copyapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new CopyFragment()).commit();
		}
	}

	// Declares a path string for URIs that you use to copy data
	private static final String COPY_PATH = "/copy";

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class CopyFragment extends Fragment {

		private ClipboardManager clipboardManager;

		private void copyToClipboard() {
			final String passwordId = "password_file.gpg";
			//final String passwordId = "some_dir/password_file.gpg";
			final Uri copyUri = Uri.parse(PasswordCopyProvider.PASSWORDS + COPY_PATH + "/" + passwordId);
			final ClipData clip = ClipData.newUri(getActivity().getContentResolver(), "Password", copyUri);
			clipboardManager.setPrimaryClip(clip);
		}

		public CopyFragment() {
		}

		@Override
		public void onAttach(final Activity activity) {
			super.onAttach(activity);
			clipboardManager = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
		}

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			final Button copyButton = (Button) rootView.findViewById(R.id.copy_button);
			copyButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(final View v) {
					copyToClipboard();
				}

			});
			return rootView;
		}
	}

}
