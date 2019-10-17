package com.yf.mesmid;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class ScanSound {
	static String MUSIC_RIGHT = "right.wav";
	static String MUSIC_ERROR = "error.wav";
	static String MUSIC_SCAN = "scan.wav";
	
	static void PlayMusic(Context context, String FileName){
		MediaPlayer Mediaplayer = new MediaPlayer();
		AssetFileDescriptor file;
		try {
			file = context.getAssets().openFd(FileName);
			Mediaplayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
			Mediaplayer.prepare();
			Mediaplayer.start();
			Mediaplayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
					mp = null;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}
}
