package com.simroth.pass.pasteapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PasteFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PasteFragment extends Fragment {

		private ClipboardManager clipboardManager;

		private TextView pasteView;

		public PasteFragment() {
		}

		private void pasteFromClipboard() {
			if (clipboardManager.hasPrimaryClip()) {
				final ClipData clip = clipboardManager.getPrimaryClip();
				if (clip != null) {
					Log.d("pass", "Requesting clipboard data as UID: " + Process.myUid());
					for (int i = 0; i < clip.getItemCount(); i++) {
						final ClipData.Item item = clip.getItemAt(i);
						final CharSequence text = item.coerceToText(getActivity());
						pasteView.append(text);
						pasteView.append("\n");
					}
				}
			}
		}

		@Override
		public void onAttach(final Activity activity) {
			super.onAttach(activity);
			clipboardManager = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
			clipboardManager
					.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

						@Override
						public void onPrimaryClipChanged() {
							pasteView.append("New primary clip available.\n");
						}
					});
		}

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			pasteView = (TextView) rootView.findViewById(R.id.paste_text);
			final Button pasteButton = (Button) rootView.findViewById(R.id.paste_button);
			pasteButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(final View v) {
					pasteFromClipboard();
				}
			});

			final Button clearButton = (Button) rootView.findViewById(R.id.clear_button);
			clearButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(final View v) {
					pasteView.setText("");
				}
			});
			return rootView;
		}

	}

}
