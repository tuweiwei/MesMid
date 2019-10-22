package com.yf.mesmid.app;

import com.yf.mesmid.R;
import com.yf.mesmid.service.DownloadService;
import com.yf.mesmid.service.DownloadService.DownloadBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author tuwei
 */
public class NotificationUpdateActivity extends Activity {
	private Button btn_cancel;
	private TextView tv_progress;
	private DownloadBinder binder;
	private boolean isBinded;
	private ProgressBar mProgressBar;
	private String ApkUrl;
	private boolean isDestroy = true;
	private MyApp app;

	private ICallbackResult callback = new ICallbackResult() {
		@Override
		public void OnBackResult(Object result) {
			if ("finish".equals(result)) {
				finish();
				return;
			}

			int i = (Integer) result;
			mProgressBar.setProgress(i);
			mHandler.sendEmptyMessage(i);
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			tv_progress.setText("当前进度 ： " + msg.what + "%");
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);

		app = (MyApp) getApplication();
		ApkUrl = getIntent().getStringExtra("apkurl");
		btn_cancel = (Button) findViewById(R.id.cancel);
		tv_progress = (TextView) findViewById(R.id.currentPos);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar1);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				binder.cancel();
				binder.cancelNotification();
				finish();
			}
		});
	}

	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBinded = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (DownloadBinder) service;
			isBinded = true;
			binder.addCallback(callback);
			binder.start();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (isDestroy && app.isDownload()) {
			Intent it = new Intent(NotificationUpdateActivity.this, DownloadService.class);
			it.putExtra("apkurl", ApkUrl);
			startService(it);
			bindService(it, conn, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		isDestroy = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isBinded) {
			unbindService(conn);
		}
		if (binder != null && binder.isCanceled()) {
			Intent it = new Intent(this, DownloadService.class);
			stopService(it);
		}
	}

	public interface ICallbackResult {
		void OnBackResult(Object result);
	}
}
