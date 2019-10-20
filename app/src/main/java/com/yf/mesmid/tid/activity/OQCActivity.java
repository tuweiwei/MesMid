package com.yf.mesmid.ui.activitys.activity;

import com.yf.mesmid.db.DatabaseOper;
import com.yf.mesmid.R;
import com.yf.mesmid.util.ScanSound;
import com.yf.mesmid.entity.IOQCInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class OQCActivity extends Activity{
	private ProgressDialog mDialog;
	private TextView TextOpertips = null;
	private boolean bEnter = false;
	
	private final String ConfigPath = Environment.getExternalStorageDirectory().getPath()+"/MESConfig.ini";
	private final int ERROR_NOEXIT = 100;
	private final int CONNECT_SUCCESS = 101;
	private final int SCANBARCODE_SUCCESS = 102;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oqc);
		mDialog = new ProgressDialog(this);
		TextOpertips = (TextView) findViewById(R.id.Text_oqcinfo);
		final EditText Editopc = (EditText) findViewById(R.id.edit_oqc);
		Editopc.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						Editopc.setText(strBuff);
						Editopc.setSelection(1);	
					}
					
				}
				if(str.endsWith("\n")){
					String str1 = str.substring(0, Len-1);
					int iPos = str1.lastIndexOf("\n");
					String strBuff = "";
					if(-1 == iPos) {
						strBuff = str1.substring(0, str1.length());
					}else{
						strBuff = str1.substring(iPos+1, str1.length());
					}
					mDialog.setMessage("����ɨ������...");
					mDialog.show();
					RScan rScan = new RScan(strBuff);
					Thread thread = new Thread(rScan);
					thread.start();
					
					Editopc.setText(str.substring(0, Len-1));
					Editopc.setSelection(Len-1);
					bEnter = true;
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		Intent intent = new Intent(getApplicationContext(), UserActivity.class);
		startActivity(intent);
	}
    
	private void SendDataMessage(int Code, Object Data, int delay){
		Message msg = handler.obtainMessage();
		msg.what = Code;
		msg.obj = Data;
		handler.sendMessageDelayed(msg, delay*1000);
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int Code = msg.what;
			if(Code == ERROR_NOEXIT){
				IOQCInfo info = (IOQCInfo)msg.obj;
				mDialog.cancel();
				TextOpertips.setTextColor(Color.RED);
				TextOpertips.setText(info.Getinfo());
				
			}
			else if(Code == CONNECT_SUCCESS){
				mDialog.setMessage((String)msg.obj);
			}
			/*��ȡSOP�������*/
			else if(Code == SCANBARCODE_SUCCESS){
				IOQCInfo info = (IOQCInfo)msg.obj;
				mDialog.setMessage(info.Getinfo());
				mDialog.cancel();
				TextOpertips.setTextColor(Color.BLUE);
				TextOpertips.setText(info.Getinfo());
			}
		};
	};
	
	boolean CheckBLTM(String strtm)
	{
		if(strtm == null) return false;
		String str = "ABCDEFGHIJKLMNOPQRSTVWUXYZ";
		if(4 == strtm.length() && -1 != str.indexOf(strtm.substring(0, 1))) return true;
		else return false;
	}
	
	class RScan implements Runnable{
		private String barcode;
		RScan(String barcode)
		{
	        this.barcode = barcode;
		}
		@Override
		public void run() {
			if(null == DatabaseOper.con)
			{
				DatabaseOper.InitDatabaseConfig(ConfigPath);
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(ERROR_NOEXIT, "���ݿ�����ʧ��", 0);
					return;
				}else SendDataMessage(CONNECT_SUCCESS, "���ݿ����ӳɹ�������ɨ������...", 0);
			}
			if(CheckBLTM(barcode)) DatabaseOper.mbBL = true;
			else DatabaseOper.mbBL = false;
			IOQCInfo info =DatabaseOper.ScanOQCBarcode(barcode);
			if(! info.Getxh().equals("100")){
				ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				SendDataMessage(ERROR_NOEXIT, info, 0);
			}
			else{
				ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
				SendDataMessage(SCANBARCODE_SUCCESS, info, 0);
			}
			
		}
	}
}
