package com.simroth.pass.copyapp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ClipDescription;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.util.Log;

public class PasswordCopyProvider extends ContentProvider {

	// Creates a Uri based on a base Uri and a record ID based on the contact's last name
	// Declares the base URI string
	static final String PASSWORDS = "content://com.simroth.pass";

	// A Uri Match object that simplifies matching content URIs to patterns.
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	// An integer to use in switching based on the incoming URI pattern
	private static final int GET_SINGLE_PASSWORD = 1;

	@Override
	public boolean onCreate() {
		// Adds a matcher for the content URI. It matches
		// "content://com.simroth.pass/copy/*"
		sURIMatcher.addURI(PASSWORDS, "copy/*", GET_SINGLE_PASSWORD);
		return true;
	}

	@Override
	public AssetFileDescriptor openTypedAssetFile(final Uri uri, final String mimeTypeFilter,
			final Bundle opts) throws FileNotFoundException {

		final int uid = Binder.getCallingUid();
		Log.d("pass", "Password request from UID " + uid + " / my UID: " + Process.myUid());

		final int pid = Binder.getCallingPid();
		Log.d("pass", "Password request from PID " + pid + " / my PID: " + Process.myPid());

		//		final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
		//		dialog.setMessage("Do you want to allow the process " + pid + " to access your password?");
		//		dialog.setTitle("Password request");
		//		dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		//
		//			@Override
		//			public void onClick(final DialogInterface dialog, final int which) {
		//			}
		//		});
		//		dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
		//
		//			@Override
		//			public void onClick(final DialogInterface dialog, final int which) {
		//			}
		//		});
		//		dialog.show();
		//		Log.d("pass", "Dialog shown");

		final String password = "foobar123";

		return new AssetFileDescriptor(openPipeHelper(uri, ClipDescription.MIMETYPE_TEXT_PLAIN, opts,
				password, new PipeDataWriter<String>() {

					@Override
					public void writeDataToPipe(final ParcelFileDescriptor output, final Uri uri,
							final String mimeType, final Bundle opts, final String args) {

						final FileOutputStream out = new FileOutputStream(output.getFileDescriptor());
						try {
							out.write(args.getBytes("UTF-8"));
						} catch (final Exception e) {
							throw new RuntimeException(e);
						} finally {
							try {
								if (out != null)
									out.close();
							} catch (final IOException ignored) {
							}
						}

					}
				}), 0, AssetFileDescriptor.UNKNOWN_LENGTH);
	}

	@Override
	public String getType(final Uri uri) {

		switch (sURIMatcher.match(uri)) {
			case GET_SINGLE_PASSWORD:
				return ClipDescription.MIMETYPE_TEXT_PLAIN;
		}
		return ClipDescription.MIMETYPE_TEXT_PLAIN;
	}

	@Override
	public String[] getStreamTypes(final Uri uri, final String mimeTypeFilter) {
		return new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN };
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		return null;
	}

	@Override
	public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(final Uri uri, final ContentValues values, final String selection,
			final String[] selectionArgs) {
		return 0;
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection,
			final String[] selectionArgs, final String sortOrder) {
		return null;
	}

}
