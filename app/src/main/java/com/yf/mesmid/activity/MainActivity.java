package com.yf.mesmid.activity;

import java.sql.SQLException;

import com.yf.mesmid.db.DatabaseOper;
import com.yf.mesmid.R;
import com.yf.mesmid.service.WifiService;
import com.yf.mesmid.entity.UpdataInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity{
	private ProgressDialog mDialog;
	private AlertDialog mErrorDialog;
	private Button Btn_sc = null;
	private Button Btn_cj = null;
	private WIFIBroadcastReceiver WifiReceiver;
	private String curversion;
	private UpdataInfo info;
	private boolean djjr = false;
	private int gdxh = 0;
	private int gxxh = 0;
	private String user = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zy);

		Btn_sc = (Button) findViewById(R.id.btn_sc);
		Btn_sc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DatabaseOper.SC_MODE = DatabaseOper.ZC_MODE;
				DatabaseOper.GX_MODE = DatabaseOper.ZC_MODE;
				Intent intent = new Intent();
				intent.putExtra("djjr", djjr);
				intent.putExtra("gdxh", gdxh);
				intent.putExtra("gxxh", gxxh);
				intent.putExtra("user", user);
				intent.setClass(getApplicationContext(), JobListActivity.class);
				startActivity(intent);
				finish();
			}
		});
		Btn_cj = (Button) findViewById(R.id.btn_cj);
		Btn_cj.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
//				intent.setClass(getApplicationContext(), OQCActivity.class);
				DatabaseOper.SC_MODE = DatabaseOper.FG_MODE;
				DatabaseOper.GX_MODE = DatabaseOper.ZC_MODE;
				intent.putExtra("djjr", djjr);
				intent.putExtra("gdxh", gdxh);
				intent.putExtra("gxxh", gxxh);
				intent.putExtra("user", user);
				intent.setClass(getApplicationContext(), JobListActivity.class);
				startActivity(intent);
				finish();
			}
		});

		curversion = getVersion();
		mDialog = new ProgressDialog(this);

		DatabaseOper.SC_MODE=DatabaseOper.ZC_MODE;
		DatabaseOper.GX_MODE=DatabaseOper.ZC_MODE;

		djjr = getIntent().getBooleanExtra("djjr", false);

		if(djjr){
			gdxh = getIntent().getIntExtra("gdxh", 0);
			gxxh = getIntent().getIntExtra("gxxh", 0);
			user = getIntent().getStringExtra("user");
		}
		
		DatabaseOper.InitDatabaseConfig(ConfigPath);

		if(DatabaseOper.ConnMode.equals(DatabaseOper.WIRE_CONN)){
			RScan rScan = new RScan(GET_UPDATEINFO);
			Thread thread = new Thread(rScan);
			thread.start();
		} else{
			WifiReceiver = new WIFIBroadcastReceiver();
			IntentFilter Wififilter = new IntentFilter();
			Wififilter.addAction(DatabaseOper.FirstWIFI_MSG);
			Wififilter.addAction(DatabaseOper.RepeatWIFI_MSG);
			registerReceiver(WifiReceiver, Wififilter);
			Intent intent = new Intent(this, WifiService.class);
			intent.putExtra("repeatquery", false);
			startService(intent);
		}
	}

	private String getVersion() {
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			return "";
		}
	}

	private boolean isNeedUpdate(String versiontext) {
		try {
			String version = info.getVersion();
			if (versiontext.equals(version)) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("" + info.getVersion());
		builder.setMessage(info.getDescription() + "\n\n" + "");
		builder.setPositiveButton("", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(null == info.getApkurl()){
					SendDataMessage(UPDATE_ERROR, "APK", 0);
					return;
				}
				app.setDownload(true);
				Intent it = new Intent(MainActivity.this, NotificationUpdateActivity.class);
				it.putExtra("apkurl", info.getApkurl());
				startActivity(it);
			}
		}).setNegativeButton("", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), XGActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private void TipError(String strInfo, final boolean bExit) {
		AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
		build.setTitle("                             \n\n\n\n");
		build.setMessage(strInfo+"\n\n");
		mErrorDialog = build.create();
		mErrorDialog.show();
		SendDataMessage(ERRORDIALOG_CANCEL, "", 3);
	}
	
	private void SendDataMessage(int Code, Object Data, int delay){
		Message msg = handler.obtainMessage();
		msg.what = Code;
		msg.obj = Data;
		handler.sendMessageDelayed(msg, delay*1000);
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			int Code = msg.what;
			if(Code == ERROR_NOEXIT){
				mDialog.cancel();
				TipError((String)msg.obj, false);
			}
			else if(Code == CONNECT_SUCCESS){
				mDialog.setMessage((String)msg.obj);
			}
			else if(ERRORDIALOG_CANCEL == Code){
				mErrorDialog.cancel();
			}
			else if(UPDATE == Code){
				mDialog.setMessage("");
				if(STRING_UPDATE.equals((String)msg.obj)) {
					showUpdateDialog();
				}else{
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), XGActivity.class);
					startActivity(intent);
					finish();
				}
			}
			else if(UPDATE_ERROR == Code){
				TipError((String)msg.obj, false);
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(WifiReceiver);
	}
	
	class RScan implements Runnable{
		private int query_mode;
		RScan(int mode) {
	        query_mode = mode;
		}
		@Override
		public void run() {
			if(null == DatabaseOper.con) {
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(ERROR_NOEXIT, "", 0);
					return;
				}else SendDataMessage(CONNECT_SUCCESS, "...", 0);
			}
			if(GET_UPDATEINFO == query_mode) {
				SendDataMessage(CONNECT_SUCCESS, "...", 0);
				UpdataInfo updatainfo = DatabaseOper.GetUpdateInfo();
				if(null == updatainfo){
					SendDataMessage(ERROR_NOEXIT, "", 0);
				}else{
					info = updatainfo;
					String strUpdate;
					if(isNeedUpdate(curversion)){
						strUpdate = STRING_UPDATE;
					}else strUpdate = STRING_NOUPDATE;
					SendDataMessage(UPDATE, strUpdate, 0);
				}
			}
			
		}
		
	}


	class WIFIBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(DatabaseOper.FirstWIFI_MSG)){
				mDialog.setMessage("...");
				RScan rScan = new RScan(GET_UPDATEINFO);
				Thread thread = new Thread(rScan);
				thread.start();
			}else if(action.equals(DatabaseOper.RepeatWIFI_MSG)){
				if(null != DatabaseOper.con){
					try {
						DatabaseOper.con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					DatabaseOper.con = null;
				}

			}

		}

	}
}
