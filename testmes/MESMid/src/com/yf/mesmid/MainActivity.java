package com.yf.mesmid;

import java.io.DataOutput;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yf.mesmid.DatabaseOper;
import com.yf.mesmid.JobListActivity.WIFIBroadcastReceiver;
import com.yf.mesupdate.MyApp;
import com.yf.mesupdate.NotificationUpdateActivity;
import com.yf.mesupdate.UpdataInfo;
import com.yftech.hw.HWLib;

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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity{
	private final String TAG = "ZY";
	private ProgressDialog mDialog;
	private AlertDialog mErrorDialog;
	
	private final String ConfigPath = Environment.getExternalStorageDirectory().getPath()+"/MESConfig.ini";
	private final int GET_UPDATEINFO = 1;
	private final int ERROR_NOEXIT = 100;
	private final int CONNECT_SUCCESS = 101;
	private final int SCANBARCODE_SUCCESS = 102;
	
	private final int ERRORDIALOG_CANCEL = 120;
	private final int UPDATE = 130;
	private final int UPDATE_ERROR = 131;
	
	private final String STRING_UPDATE = "APP有更新";
	private final String STRING_NOUPDATE = "APP无更新";
	
	private final String XGUUID1 = "5052455354880001";
	private final String XGUUID2 = "5052455354880002";
	private final String XGUUID3 = "5052455354880003";
	
	private final String GWUUID1 = "5052455354880011";
	private final String GWUUID2 = "5052455354880012";
	private final String GWUUID3 = "5052455354880013";
	
	private final String WXUUID1 = "6052455354880021";
	private final String WXUUID2 = "6052455354880022";
	private final String WXUUID3 = "6052455354880023";
	private final String WXUUID4 = "6052455354880024";//6052455354260007
	
	private final String OQCUUID1 = "6062455354880021";
	private final String OQCUUID2 = "6062455354880022";
	private final String OQCUUID3 = "6062455354880023";//5052455354260007 6062455354880023
	private final String OQCUUID4 = "6062455354880024";
	private final String OQCUUID5 = "6062455354880025";
	
	private final String IQCUUID1 = "50524553542400CC";
	private final String IQCUUID2 = "50524553542400CD";
	private final String IQCUUID3 = "50524553542400CE";//
	private final String IQCUUID4 = "50524553542400CF";
	private final String IQCUUID5 = "50524553542400D0";
	private final String IQCUUID6 = "50524553542400D1";//
	private final String IQCUUID7 = "50524553542400D2";
	private final String IQCUUID8 = "50524553542400D3";
	private final String IQCUUID9 = "50524553542400D4";
	private final String IQCUUID10 = "50524553542400D5";
	private Button Btn_sc = null;
	private Button Btn_cj = null;
	
	private WIFIBroadcastReceiver WifiReceiver;
	
	private MyApp app;
	private int currentVersionCode;
	private String curversion;
	private UpdataInfo info;
	
	private String UUID;
	//----点检APK进入
	private boolean djjr = false;
	private int gdxh = 0;
	private int gxxh = 0;
	private String user = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zy);
		  //查找以Java开头,任意结尾的字符串 
		 // Pattern pattern = Pattern.compile("[A-Z]");
		  //Matcher matcher = pattern.matcher("ABCDEFGHIJKLMNOPQRSTVWUXY");
		  //boolean b = matcher.matches(); //当条件满足时，将返回true，否则返回false 
		 // b = matcher.find();
		Btn_sc = (Button) findViewById(R.id.btn_sc);
		Btn_sc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(UUID.equals(OQCUUID1) || UUID.equals(OQCUUID2) || UUID.equals(OQCUUID3)
						|| UUID.equals(OQCUUID4) || UUID.equals(OQCUUID5)) {
					DatabaseOper.SC_MODE = DatabaseOper.OQC_MODE;
					DatabaseOper.GX_MODE = DatabaseOper.OQC_MODE;
				}
				else {
					DatabaseOper.SC_MODE = DatabaseOper.ZC_MODE;
					DatabaseOper.GX_MODE = DatabaseOper.ZC_MODE;
				}
				Intent intent = new Intent();
				intent.putExtra("djjr", djjr);
				intent.putExtra("gdxh", gdxh);
				intent.putExtra("gxxh", gxxh);
				intent.putExtra("user", user);
				intent.setClass(getApplicationContext(), JobListActivity.class);
				startActivity(intent);
				/*Intent intent = new Intent();
				intent.setClass(getApplicationContext(), KeyBoard.class);
				startActivity(intent);*/
				finish();
				
			}
		});
		Btn_cj = (Button) findViewById(R.id.btn_cj);
		Btn_cj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				if(UUID.equals(OQCUUID1) || UUID.equals(OQCUUID2) || UUID.equals(OQCUUID3)
						|| UUID.equals(OQCUUID4) || UUID.equals(OQCUUID5)){
					intent.setClass(getApplicationContext(), OQCActivity.class);
				}else{
					DatabaseOper.SC_MODE = DatabaseOper.FG_MODE;
					DatabaseOper.GX_MODE = DatabaseOper.ZC_MODE;
					intent.putExtra("djjr", djjr);
					intent.putExtra("gdxh", gdxh);
					intent.putExtra("gxxh", gxxh);
					intent.putExtra("user", user);
					intent.setClass(getApplicationContext(), JobListActivity.class);
				}
				startActivity(intent);
				finish();
				
			}
		});

		app = (MyApp) getApplication();
		curversion = getVersion();
		mDialog = new ProgressDialog(this);
		UUID = getMyUUID();
		//UUID="5052455354240010";
		//UUID=IQCUUID1;
		//UUID="";
		if(null == UUID){
			TipError("请先烧录UUID", false);
			return;
		}
		else{}
		//初始化生产模式,都为正常生产
		DatabaseOper.SC_MODE=DatabaseOper.ZC_MODE;
		DatabaseOper.GX_MODE=DatabaseOper.ZC_MODE;
		//--点检进入
		djjr = getIntent().getBooleanExtra("djjr", false);
		Log.i("MesMid Main", ""+djjr);
		if(djjr){
			gdxh = getIntent().getIntExtra("gdxh", 0);
			gxxh = getIntent().getIntExtra("gxxh", 0);
			user = getIntent().getStringExtra("user");
			Log.i("MesMid Main", ""+gdxh);
			Log.i("MesMid Main", ""+gxxh);
			Log.i("MesMid Main", ""+user);
			//Toast.makeText(getApplicationContext(), "进入JOB", 5000).show();
		}
		
		DatabaseOper.InitDatabaseConfig(ConfigPath);
		if(DatabaseOper.ConnMode.equals(DatabaseOper.WIRE_CONN)){
			RScan rScan = new RScan(GET_UPDATEINFO);
			Thread thread = new Thread(rScan);
			thread.start();
		}
		else{
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
	
	private String getMyUUID(){
    	String UUID = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        
        String ret=(!TextUtils.isEmpty(UUID) ? UUID : null);
        return ret;
    }
	
	/**
	 * @return 获取当前应用程序的版本号
	 */
	private String getVersion() {
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {//

			e.printStackTrace();
			return "版本号未知";
		}
	}
	
	/**
	 * 
	 * @param versiontext
	 *            当前客户端的版本号信息
	 * @return 是否需要更新
	 */
	private boolean isNeedUpdate(String versiontext) {
		//GetUpdataInfo updateinfo = new GetUpdataInfo();//从xml中获取
		try {
			//info = updateinfo.getUpdataInfo();
			String version = info.getVersion();
			if (versiontext.equals(version)) {
				Log.i(TAG, "版本相同,无需升级, 进入主界面");
				//loadMainUI();
				return false;
			} else {
				Log.i(TAG, "版本不同,需要升级");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			SendDataMessage(UPDATE_ERROR, "获取更新信息异常", 0);
			Log.i(TAG, "获取更新信息异常, 进入主界面");
			//loadMainUI();
			return false;
		}

	}
	
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("检测到新版本 V" + info.getVersion());
		builder.setMessage(info.getDescription() + "\n\n" + "是否下载更新?");
		builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(null == info.getApkurl()){
					SendDataMessage(UPDATE_ERROR, "APK地址获取失败", 0);
					return;
				}
				app.setDownload(true);
				Intent it = new Intent(MainActivity.this, NotificationUpdateActivity.class);
				it.putExtra("apkurl", info.getApkurl());
				startActivity(it);
                //----------MapApp.isDownload = true;
				//----------loadMainUI();
				//----------finish();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//转向各个activity
				Intent intent = new Intent();
				if(UUID.equals(XGUUID1) || UUID.equals(XGUUID2) || UUID.equals(XGUUID3)){
					intent.setClass(getApplicationContext(), XGActivity.class);
					//intent.setClass(getApplicationContext(), JobListActivity.class);
				}else if(UUID.equals(GWUUID1) || UUID.equals(GWUUID2) || UUID.equals(GWUUID3)){
					intent.setClass(getApplicationContext(), GWActivity.class);
					//intent.setClass(getApplicationContext(), JobListActivity.class);
				}else if(UUID.equals(WXUUID1) || UUID.equals(WXUUID2) || UUID.equals(WXUUID3) || UUID.equals(WXUUID4)){
					intent.setClass(getApplicationContext(), WXActivity.class);
					//intent.setClass(getApplicationContext(), JobListActivity.class);
				}else if(UUID.equals(OQCUUID1) || UUID.equals(OQCUUID2) || UUID.equals(OQCUUID3)
						|| UUID.equals(OQCUUID4) || UUID.equals(OQCUUID5)){
					Btn_sc.setText("OQC 生  产" );
					Btn_cj.setText("OQC 售  后" );
					Btn_cj.setVisibility(View.VISIBLE);
					Btn_sc.setVisibility(View.VISIBLE);
					return;
				}else if(UUID.equals(IQCUUID1) || UUID.equals(IQCUUID2) || UUID.equals(IQCUUID3)
						|| UUID.equals(IQCUUID4) || UUID.equals(IQCUUID5) || UUID.equals(IQCUUID6) || UUID.equals(IQCUUID7)
						|| UUID.equals(IQCUUID8) || UUID.equals(IQCUUID9) || UUID.equals(IQCUUID10)){
					intent.setClass(getApplicationContext(), IQCActivity.class);
					intent.putExtra("deviceid", UUID);
				}else{
					Btn_sc.setText("正常生产" );
					Btn_cj.setText("线内返工" );
					Btn_cj.setVisibility(View.VISIBLE);
					Btn_sc.setVisibility(View.VISIBLE);
					return;
					/*intent.setClass(getApplicationContext(), JobListActivity.class);
					intent.putExtra("djjr", djjr);
					intent.putExtra("gdxh", gdxh);
					intent.putExtra("gxxh", gxxh);
					intent.putExtra("user", user);*/	
				}
				startActivity(intent);
				finish();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	//OQC临时更新
	private void showUpdateDialog1() {
		Intent intent = new Intent();
		if(UUID.equals(XGUUID1) || UUID.equals(XGUUID2) || UUID.equals(XGUUID3)){
			intent.setClass(getApplicationContext(), XGActivity.class);
			//intent.setClass(getApplicationContext(), JobListActivity.class);
		}else if(UUID.equals(GWUUID1) || UUID.equals(GWUUID2) || UUID.equals(GWUUID3)){
			intent.setClass(getApplicationContext(), GWActivity.class);
			//intent.setClass(getApplicationContext(), JobListActivity.class);
		}else if(UUID.equals(WXUUID1) || UUID.equals(WXUUID2) || UUID.equals(WXUUID3) || UUID.equals(WXUUID4)){
			intent.setClass(getApplicationContext(), WXActivity.class);
			//intent.setClass(getApplicationContext(), JobListActivity.class);
		}else if(UUID.equals(OQCUUID1) || UUID.equals(OQCUUID2) || UUID.equals(OQCUUID3)
				|| UUID.equals(OQCUUID4) || UUID.equals(OQCUUID5)){
			Btn_sc.setText("OQC 生  产" );
			Btn_cj.setText("OQC 售  后" );
			Btn_cj.setVisibility(View.VISIBLE);
			Btn_sc.setVisibility(View.VISIBLE);
			return;
		}else if(UUID.equals(IQCUUID1) || UUID.equals(IQCUUID2) || UUID.equals(IQCUUID3)
				|| UUID.equals(IQCUUID4) || UUID.equals(IQCUUID5) || UUID.equals(IQCUUID6) || UUID.equals(IQCUUID7)
				|| UUID.equals(IQCUUID8) || UUID.equals(IQCUUID9) || UUID.equals(IQCUUID10)){
			intent.setClass(getApplicationContext(), IQCActivity.class);
			intent.putExtra("deviceid", UUID);
		}else{
			Btn_sc.setText("正常生产" );
			Btn_cj.setText("线内返工" );
			Btn_cj.setVisibility(View.VISIBLE);
			Btn_sc.setVisibility(View.VISIBLE);
			return;
			/*intent.setClass(getApplicationContext(), JobListActivity.class);
			intent.putExtra("djjr", djjr);
			intent.putExtra("gdxh", gdxh);
			intent.putExtra("gxxh", gxxh);
			intent.putExtra("user", user);*/
		}
		startActivity(intent);
		finish();
	}
	
	private void TipError(String strInfo, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
		build.setTitle("异常提示                              \n\n\n\n");
		build.setMessage(strInfo+"\n\n");
		/*build.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(bExit){
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(0);
				}
				
			}
		});*/
		mErrorDialog = build.create();
		mErrorDialog.show();
		SendDataMessage(ERRORDIALOG_CANCEL, "错误对话框消失", 3);
	}
	
	private void SendDataMessage(int Code, Object Data, int delay){
		Message msg = handler.obtainMessage();
		msg.what = Code;
		msg.obj = Data;
		handler.sendMessageDelayed(msg, delay*1000);
	}
	
	class WIFIBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(DatabaseOper.FirstWIFI_MSG)){
				mDialog.setMessage("正在连接数据库...");
				RScan rScan = new RScan(GET_UPDATEINFO);
				Thread thread = new Thread(rScan);
				thread.start();
			}else if(action.equals(DatabaseOper.RepeatWIFI_MSG)){
				if(null != DatabaseOper.con){
					try {
						DatabaseOper.con.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DatabaseOper.con = null;
				}
				
			}
			
		}
		
	}
	
	Handler handler = new Handler(){
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
				mDialog.setMessage("检查版本更新成功");
				if(STRING_UPDATE.equals((String)msg.obj)) {
					showUpdateDialog();
				}else{
					//转向各个activity
					Intent intent = new Intent();
					if(UUID.equals(XGUUID1) || UUID.equals(XGUUID2) || UUID.equals(XGUUID3)){
						intent.setClass(getApplicationContext(), XGActivity.class);
					}else if(UUID.equals(GWUUID1) || UUID.equals(GWUUID2) || UUID.equals(GWUUID3)){
						intent.setClass(getApplicationContext(), GWActivity.class);
						//intent.setClass(getApplicationContext(), JobListActivity.class);
					}else if(UUID.equals(OQCUUID1) || UUID.equals(OQCUUID2) || UUID.equals(OQCUUID3)
							|| UUID.equals(OQCUUID4) || UUID.equals(OQCUUID5)){
						Btn_sc.setText("OQC 生  产" );
						Btn_cj.setText("OQC 售  后" );
						Btn_cj.setVisibility(View.VISIBLE);
						Btn_sc.setVisibility(View.VISIBLE);
						return;
					}else if(UUID.equals(IQCUUID1) || UUID.equals(IQCUUID2) || UUID.equals(IQCUUID3)
							|| UUID.equals(IQCUUID4) || UUID.equals(IQCUUID5) || UUID.equals(IQCUUID6) || UUID.equals(IQCUUID7)
							|| UUID.equals(IQCUUID8) || UUID.equals(IQCUUID9) || UUID.equals(IQCUUID10)){
						intent.setClass(getApplicationContext(), IQCActivity.class);
						intent.putExtra("deviceid", UUID);
					}else{
						Btn_sc.setText("正常生产" );
						Btn_cj.setText("线内返工" );
						Btn_cj.setVisibility(View.VISIBLE);
						Btn_sc.setVisibility(View.VISIBLE);
						return;
						/*intent.setClass(getApplicationContext(), JobListActivity.class);
						intent.putExtra("djjr", djjr);
						intent.putExtra("gdxh", gdxh);
						intent.putExtra("gxxh", gxxh);
						intent.putExtra("user", user);*/
					}
					startActivity(intent);
					finish();
				}
				
				
			}
			else if(UPDATE_ERROR == Code){
				TipError((String)msg.obj, false);
			}
		};
	};
	
	protected void onDestroy() {
		super.onDestroy();
		if(!DatabaseOper.ConnMode.equals(DatabaseOper.WIRE_CONN)){
			unregisterReceiver(WifiReceiver);
		}
	
	};
	
	class RScan implements Runnable{
		private int query_mode;
		RScan(int mode)
		{
	        query_mode = mode;
		}
		@Override
		public void run() {
			if(null == DatabaseOper.con)
			{
				//DatabaseOper.InitDatabaseConfig(ConfigPath);
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(ERROR_NOEXIT, "数据库连接失败", 0);
					return;
				}else SendDataMessage(CONNECT_SUCCESS, "数据库连接成功，正在扫描条码...", 0);
			}
			if(GET_UPDATEINFO == query_mode)
			{
				SendDataMessage(CONNECT_SUCCESS, "正在检查版本更新...", 0);
				UpdataInfo updatainfo = DatabaseOper.GetUpdateInfo();
				if(null == updatainfo){
					SendDataMessage(ERROR_NOEXIT, "检查版本更新失败", 0);
				}else{
					info = updatainfo;
					String strUpdate = null;
					if(isNeedUpdate(curversion)){
						strUpdate = STRING_UPDATE;
					}else strUpdate = STRING_NOUPDATE;
					SendDataMessage(UPDATE, strUpdate, 0);
				}
				//SendDataMessage(UPDATE, "检查版本更新成功", 0);
			}
			
		}
		
	}
}
