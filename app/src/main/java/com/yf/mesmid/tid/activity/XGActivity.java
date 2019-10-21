package com.yf.mesmid.tid.activity;

import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yf.mesmid.barcodebind.UserActivity;
import com.yf.mesmid.consts.MyConsts;
import com.yf.mesmid.db.DatabaseOper;
import com.yf.mesmid.R;
import com.yf.mesmid.tid.adapter.XGAdapt;
import com.yf.mesmid.util.ScanSound;
import com.yf.mesmid.entity.XGInfo;

/**
 * @author tuwei
 */
public class XGActivity extends Activity{
	private EditText EditXG;
	private boolean bEnter = false;
	private TextView TextOpertips;
	private ProgressDialog mDialog;
	private AlertDialog mErrorDialog;
	private RadioButton RBXRadio;
	private RadioButton HWRadio;
	private RadioButton JQJBRadio;
	private RadioButton RGJBRadio;
	private RadioButton CXRadio;
	private ListView mlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xg);
		mDialog = new ProgressDialog(this);
		RBXRadio = (RadioButton) findViewById(R.id.radioButton_rbx);
		RBXRadio.setChecked(true);
		RBXRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					Intent intent = new Intent(getApplicationContext(), UserActivity.class);
					startActivity(intent);
				}
				
			}
		});
		HWRadio = (RadioButton) findViewById(R.id.radioButton_hw);
		HWRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					Intent intent = new Intent(getApplicationContext(), UserActivity.class);
					startActivity(intent);
				}
				
			}
		});
		JQJBRadio = (RadioButton) findViewById(R.id.radioButton_jqjb);
		JQJBRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					Intent intent = new Intent(getApplicationContext(), UserActivity.class);
					startActivity(intent);
				}
				
			}
		});
		RGJBRadio = (RadioButton) findViewById(R.id.radioButton_rgjb);
		CXRadio = (RadioButton) findViewById(R.id.radioButton_cx);
		CXRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1) Toast.makeText(XGActivity.this, "扫描工号即代表查询全部信息", 4000).show();
				
			}
		});
		TextOpertips = (TextView) findViewById(R.id.Text_xginfo);
		mlist = (ListView) findViewById(R.id.listView_xg);
		EditXG = (EditText) findViewById(R.id.edit_xg);
		EditXG.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						EditXG.setText(strBuff);
						EditXG.setSelection(1);	
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
					String state = null;
					if(RBXRadio.isChecked()) state = "1";
					if(HWRadio.isChecked()) state = "2";
					if(JQJBRadio.isChecked()) state = "3";
					if(RGJBRadio.isChecked()) state = "4";
					if(CXRadio.isChecked()) state = "5";
					mDialog.setMessage("正在扫描条码......");
					mDialog.show();
					RScan rScan = new RScan(strBuff, state);
					Thread thread = new Thread(rScan);
					thread.start();
					
					EditXG.setText(str.substring(0, Len-1));
					EditXG.setSelection(Len-1);
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
	
	private void TipError(String strInfo, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(XGActivity.this);
		build.setTitle("sssbbb");
		build.setMessage(strInfo+"\n\n");
		/*build.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
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
		SendDataMessage(MyConsts.ERRORDIALOG_CANCEL, "sb", 3);
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
			if(Code == MyConsts.ERROR_NOEXIT){
				String strResult = (String)msg.obj;
				mDialog.cancel();
				TextOpertips.setTextColor(Color.RED);
				TextOpertips.setText(strResult);
				TipError(strResult, false);
			}
			else if(Code == MyConsts.CONNECT_SUCCESS){
				mDialog.setMessage((String)msg.obj);
			}
			else if(Code == MyConsts.SCANBARCODE_SUCCESS){
				String strResult = (String)msg.obj;
				mDialog.setMessage(strResult);
				mDialog.cancel();
				TextOpertips.setTextColor(Color.BLUE);
				TextOpertips.setText(strResult);
			}

			else if(Code == MyConsts.CX_SUCCESS){
				mDialog.cancel();
				TextOpertips.setTextColor(Color.GREEN);
				TextOpertips.setText("��ѯ�ɹ�");
				List<XGInfo> list = (List<XGInfo>) msg.obj;
				XGAdapt adapt = new XGAdapt(getApplicationContext(), list);
				mlist.setAdapter(adapt);
			}
			else if(MyConsts.ERRORDIALOG_CANCEL == Code){
				mErrorDialog.cancel();
			}
		};
	};
	
	class RScan implements Runnable{
		private String state;
		private String barcode;
		RScan(String barcode, String state)
		{
	        this.barcode = barcode;
	        this.state = state;
		}
		@Override
		public void run() {
			if(null == DatabaseOper.con){
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(MyConsts.ERROR_NOEXIT, "���ݿ�����ʧ��", 0);
					return;
				}else SendDataMessage(MyConsts.CONNECT_SUCCESS, "数据库连接成功，正在扫描条码", 0);
			}
			if(state.equals("5")){
				List<XGInfo> list = DatabaseOper.CXXGInfo(barcode);
				if(null == list){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(MyConsts.ERROR_NOEXIT, "查询异常", 0);
				}else{
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
					SendDataMessage(MyConsts.CX_SUCCESS, list, 0);
				}
			}else{
				int xh =DatabaseOper.ScanSMTInfo(barcode, "0", state, "0");
				if(100 != xh){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(MyConsts.ERROR_NOEXIT, DatabaseOper.ScanResult, 0);
				}
				else{
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
					SendDataMessage(MyConsts.SCANBARCODE_SUCCESS, DatabaseOper.ScanResult, 0);
				}
			}
		}
	}
}
