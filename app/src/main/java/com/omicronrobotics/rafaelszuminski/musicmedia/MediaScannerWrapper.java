package com.omicronrobotics.rafaelszuminski.musicmedia;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;

final class MyMediaScannerConnectionClient
		implements MediaScannerConnection.MediaScannerConnectionClient {

	private String mFilename;
	private String mMimetype;
	private MediaScannerConnection mConn;

	public MyMediaScannerConnectionClient
			(Context ctx, File file, String mimetype) {
		this.mFilename = file.getAbsolutePath();
		mConn = new MediaScannerConnection(ctx, this);
		mConn.connect();
	}

	public MyMediaScannerConnectionClient(String absolutePath, String s) {

	}



	@Override
	public void onMediaScannerConnected() {
		mConn.scanFile(mFilename, mMimetype);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		mConn.disconnect();
		Log.d("MediaScan","Done/onScanComplete");
	}
}
