package com.yf.mesmid.barcodebind;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.yf.mesmid.db.DatabaseOper;
import com.yf.mesmid.R;
import com.yf.mesmid.util.ScanSound;
import com.yf.mesmid.entity.SopInfo;

public class UserActivity  extends Activity{
	private boolean bEnter = false;
	private String sopfile = null;
	private String deviceid = null;
	private String gd = null;
	private String gydm = null;
	private String jxbm = null;
	private String batch = null;
	private String moeid = null;
	private String procedurexh = null;
	private String proceduregxmc = null;
	private String[] Nums = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	private AlertDialog mErrorDialog;
	private final int ERRORDIALOG_CANCEL = 120;
	private final int ERROR_NOEXIT = 100;
	private final int CONNECT_SUCCESS = 101;
	private final int SCANBARCODE_SUCCESS = 102;
	private final int SCANGETINITIA_SUCCESS = 105;
    private final int SCAN_GET_INITIAL  = 142;
	
	private final int MODE_GET_INITIAL  = 233;
	
	private ProgressDialog mDialog;
	EditText EditUser;
	private final String ConfigPath = Environment.getExternalStorageDirectory().getPath()+"/MESConfig.ini";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);
		sopfile = getIntent().getStringExtra("sopfile");
		deviceid = getIntent().getStringExtra("deviceid");
		gd = getIntent().getStringExtra("gd");
		gydm = getIntent().getStringExtra("gydm");
		jxbm = getIntent().getStringExtra("jxbm");
		batch = getIntent().getStringExtra("batch");
		moeid = getIntent().getStringExtra("moeid");
		procedurexh = getIntent().getStringExtra("procedurexh");
		proceduregxmc = getIntent().getStringExtra("proceduregxmc");
		//final EditText EditUser = (EditText) findViewById(R.id.edituser);
		EditUser = (EditText) findViewById(R.id.edituser);
        EditUser.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						EditUser.setText(strBuff);
						EditUser.setSelection(1);	
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
					EditUser.setText(str.substring(0, Len-1));
					EditUser.setSelection(Len-1);
					bEnter = true;
					if(9 == strBuff.length() && strBuff.startsWith("Y") 
							&& CheckUser(strBuff.substring(1))) {
						DatabaseOper.User = strBuff;
						DatabaseOper.mbScanUser = false;
						DatabaseOper.mCount = 0;
						DatabaseOper.mBatchCodeCount= 0;
						//����SOP�Ŵ������
						if(gd != null && sopfile  != null && deviceid  != null){
							//��ȡ��ʼ������
							Intent intent = null;
							if(MesLsData.bYuLang) intent = new Intent(UserActivity.this, SopYuLangActivity.class);
							else intent = new Intent(UserActivity.this, SopActivity.class);
							intent.putExtra("sopfile", sopfile);
							intent.putExtra("deviceid", deviceid);
							intent.putExtra("gd", gd);
							intent.putExtra("gydm", gydm);
							intent.putExtra("jxbm", jxbm);
							intent.putExtra("batch", batch);
							intent.putExtra("moeid", moeid);
							intent.putExtra("procedurexh", procedurexh);
							intent.putExtra("proceduregxmc", proceduregxmc);
							startActivity(intent);
							
							/*RGetInitial rGetInitial = new RGetInitial(MODE_GET_INITIAL);
							Thread thread = new Thread(rGetInitial);
							thread.start();*/
						}	
						finish();
					}else{
						//Toast.makeText(UserActivity.this, "ɨ�蹤������, ������ɨ��", Toast.LENGTH_LONG).show();
						ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
						TipError("\nɨ�蹤������, ������ɨ��\n\n", false);
					}
				}	 
		
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
        
        mDialog = new ProgressDialog(this);
	}
	
	private boolean CheckUser(String strUser)
	{
		if(null == strUser) return false;
		for(int i = 0; i < strUser.length(); i++)
		{
			String strNum = strUser.substring(i, i+1);
			/*Arrays���в���*/
			if(0 > Arrays.binarySearch(Nums, strNum) ){
				if(0 == i && strNum.equals("L"));
				else return false;
			} 
		}
		return true;
	}
	
	private void TipError(String strInfo, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(UserActivity.this);
		build.setTitle("�쳣��ʾ");
		build.setMessage(strInfo);
		mErrorDialog = build.create();
		mErrorDialog.show();
		SendDataMessage(ERRORDIALOG_CANCEL, "����Ի�����ʧ", 3);
	}
	
	private void SendDataMessage(int Code, Object Data, int delay){
		Message msg = handler.obtainMessage();
		msg.what = Code;
		msg.obj = Data;
		handler.sendMessageDelayed(msg, delay*1000);
		//handler.s
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int Code = msg.what;
			if(ERRORDIALOG_CANCEL == Code){
				mErrorDialog.cancel();
			}
			else if(ERROR_NOEXIT == Code){
				try {
					mDialog.cancel();
				} catch (Exception e) {
					e.printStackTrace();
				}
				TipError((String)msg.obj, false);
			}
			else if(SCANGETINITIA_SUCCESS == Code){
				try {
					mDialog.setMessage("��ȡ��ʼ���ݳɹ�");
					mDialog.cancel();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				DatabaseOper.mInfo= (SopInfo) msg.obj;
				DatabaseOper.mInfo.setModel(jxbm);
				DatabaseOper.mInfo.setJopnumber(gd);
				DatabaseOper.mInfo.setBatch(batch);
				DatabaseOper.mInfo.setPN(gydm);
				
				Intent intent = null;
				if(MesLsData.bYuLang) intent = new Intent(UserActivity.this, SopYuLangActivity.class);
				else intent = new Intent(UserActivity.this, SopActivity.class);
				intent.putExtra("sopfile", sopfile);
				intent.putExtra("deviceid", deviceid);
				intent.putExtra("gd", gd);
				intent.putExtra("gydm", gydm);
				intent.putExtra("jxbm", jxbm);
				intent.putExtra("batch", batch);
				intent.putExtra("moeid", moeid);
				intent.putExtra("procedurexh", procedurexh);
				intent.putExtra("proceduregxmc", proceduregxmc);
				startActivity(intent);
				finish();
			}
			else if(CONNECT_SUCCESS == Code){
				try {
					mDialog.setMessage((String)msg.obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	};
	
	class RGetInitial implements Runnable{
		int Mode;
		RGetInitial(int mode)
		{
	        this.Mode=mode;
		}
		@Override
		public void run() {
			if(null == DatabaseOper.con)
			{
				//DatabaseOper.InitDatabaseConfig(ConfigPath);
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(ERROR_NOEXIT, "���ݿ�����ʧ��", 0);
					return;
				}else SendDataMessage(CONNECT_SUCCESS, "���ݿ����ӳɹ�������ɨ������...", 0);
			}
			if(this.Mode==MODE_GET_INITIAL){
				SopInfo info = null;
				info = DatabaseOper.GetInitial(deviceid, gydm, moeid,procedurexh);
				if(null == info){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, "��ȡ��ʼ����ʧ��", 0);
				}
				else{
					SendDataMessage(SCANGETINITIA_SUCCESS, info, 0);
				}
			}
			
			
		}
		
	}
}
