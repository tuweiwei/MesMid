package com.yf.mesmid;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class GWActivity extends Activity{
	private final String TAG = "GWActivity";
	private EditText Editgw;
	private boolean bEnter = false;
	private TextView TextOpertips;
	private ProgressDialog mDialog;
	private AlertDialog mErrorDialog;
	
	private final String ConfigPath = Environment.getExternalStorageDirectory().getPath()+"/MESConfig.ini";
	private final int ERROR_NOEXIT = 100;
	private final int CONNECT_SUCCESS = 101;
	private final int SCANBARCODE_SUCCESS = 102;
	private final int CX_SUCCESS = 103;
	
	private final int ERRORDIALOG_CANCEL = 120;
	
	private RadioButton ZLOKRadio;
	private RadioButton ZLNGRadio;
	private RadioButton KBOKRadio;
	private RadioButton KBNGRadio;
	private RadioButton CXRadio;
	
	private CheckBox CXCheck;
	private boolean bZLCheckOK = true;
	private boolean bKBCheckOK = true;
	
	private ListView mlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gw);
		mDialog = new ProgressDialog(this);
		ZLOKRadio = (RadioButton) findViewById(R.id.radioButton_zlok);
		ZLOKRadio.setChecked(true);
		/*ZLOKRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					CXRadio.setChecked(false);
					ZLOKRadio.setChecked(true);
					Log.i(TAG, "ZLOKRadio CXRadio false");
				}else Log.i(TAG, "ZLOKRadio true");
				
			}
		});*/
		
		ZLNGRadio = (RadioButton) findViewById(R.id.radioButton_zlng);
		/*ZLNGRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					CXRadio.setChecked(false);
					CXRadio.setChecked(true);
					Log.i(TAG, "ZLNGRadio CXRadio false");
				}else Log.i(TAG, "ZLNGRadio true");
				
			}
		});*/
		KBOKRadio = (RadioButton) findViewById(R.id.radioButton_kbok);
		KBOKRadio.setChecked(true);
		/*KBOKRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					CXRadio.setChecked(false);
					KBOKRadio.setChecked(true);
					Log.i(TAG, "KBOKRadio CXRadio false");
				}else Log.i(TAG, "KBOKRadio true");
				
			}
		});*/
		
		KBNGRadio = (RadioButton) findViewById(R.id.radioButton_kbng);
		/*KBNGRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					CXRadio.setChecked(false);
					KBNGRadio.setChecked(true);
					Log.i(TAG, "KBNGRadio CXRadio false");
				}else Log.i(TAG, "KBNGRadio true");
				
			}
		});*/
		CXRadio = (RadioButton) findViewById(R.id.radioButton_cx);
		CXRadio.setVisibility(View.GONE);
		CXRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					ZLOKRadio.setChecked(false);
					ZLNGRadio.setChecked(false);
					KBOKRadio.setChecked(false);
					KBNGRadio.setChecked(false);
					CXRadio.setChecked(false);
					Log.i(TAG, "CXRadio ZLOKRadio  ZLNGRadio KBOKRadio KBNGRadio false");
				}else Log.i(TAG, "CXRadio true");
				
			}
		});
		CXCheck = (CheckBox) findViewById(R.id.checkBox_cx);
		CXCheck.setChecked(false);
		CXCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				/*if(arg1){
					bZLCheckOK = ZLOKRadio.isChecked();
					bKBCheckOK = KBOKRadio.isChecked();
					ZLOKRadio.setChecked(false);
					ZLNGRadio.setChecked(false);
					KBOKRadio.setChecked(false);
					KBNGRadio.setChecked(false);
				}
				else{
					if(bZLCheckOK){
						ZLNGRadio.setChecked(!bZLCheckOK);
						ZLOKRadio.setChecked(bZLCheckOK);					
					}else{
						ZLOKRadio.setChecked(bZLCheckOK);
						ZLNGRadio.setChecked(!bZLCheckOK);
					}
					if(bKBCheckOK){
						KBNGRadio.setChecked(!bKBCheckOK);
						KBOKRadio.setChecked(bKBCheckOK);
					}else{
						KBOKRadio.setChecked(bKBCheckOK);
						KBNGRadio.setChecked(!bKBCheckOK);
					}	
					
				}*/
				if(arg1) Toast.makeText(GWActivity.this, "扫描工号即代表查询全部信息", 4000).show();
				ZLOKRadio.setEnabled(!arg1);
				ZLOKRadio.setTextColor(arg1?Color.WHITE:Color.BLACK);
				ZLNGRadio.setEnabled(!arg1);
				ZLNGRadio.setTextColor(arg1?Color.WHITE:Color.BLACK);
				KBOKRadio.setEnabled(!arg1);
				KBOKRadio.setTextColor(arg1?Color.WHITE:Color.BLACK);
				KBNGRadio.setEnabled(!arg1);
				KBNGRadio.setTextColor(arg1?Color.WHITE:Color.BLACK);
				
			}
		});
		
		TextOpertips = (TextView) findViewById(R.id.Text_gwinfo);
		mlist = (ListView) findViewById(R.id.listView_gw);
		Editgw = (EditText) findViewById(R.id.edit_gw);
		//EditSop.setText("S1311000124");
		Editgw.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						Editgw.setText(strBuff);
						Editgw.setSelection(1);	
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
					String state = null; String kbstate = null;
					if(ZLOKRadio.isChecked()) state = "1";
					if(ZLNGRadio.isChecked()) state = "0";
					if(KBOKRadio.isChecked()) kbstate = "1";
					if(KBNGRadio.isChecked()) kbstate = "0";
					if(CXRadio.isChecked()) state = "2";
					if(CXCheck.isChecked()) state = "2";
					mDialog.setMessage("正在扫描条码...");
					mDialog.show();
					RScan rScan = new RScan(strBuff, state, kbstate);
					Thread thread = new Thread(rScan);
					thread.start();
					
					Editgw.setText(str.substring(0, Len-1));
					Editgw.setSelection(Len-1);
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
		AlertDialog.Builder build = new AlertDialog.Builder(GWActivity.this);
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
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int Code = msg.what;
			if(Code == ERROR_NOEXIT){
				String strResult = (String)msg.obj;
				mDialog.cancel();
				TextOpertips.setTextColor(Color.RED);
				TextOpertips.setText(strResult);
				TipError(strResult, false);
			}
			else if(Code == CONNECT_SUCCESS){
				mDialog.setMessage((String)msg.obj);
			}
			/*获取SOP数据完成*/
			else if(Code == SCANBARCODE_SUCCESS){
				String strResult = (String)msg.obj;
				mDialog.setMessage(strResult);
				mDialog.cancel();
				TextOpertips.setTextColor(Color.BLUE);
				TextOpertips.setText(strResult);
			}
			/*查询成功*/
			else if(Code == CX_SUCCESS){
				mDialog.cancel();
				TextOpertips.setTextColor(Color.GREEN);
				TextOpertips.setText("查询成功");
				List<GWInfo> list = (List<GWInfo>) msg.obj;
				GWAdapt adapt = new GWAdapt(getApplicationContext(), list);
				mlist.setAdapter(adapt);
			}
			else if(ERRORDIALOG_CANCEL == Code){
				mErrorDialog.cancel();
			}
		};
	};
	
	class RScan implements Runnable{
		private String state;
		private String kbstate;
		private String barcode;
		RScan(String barcode, String state, String kbstate)
		{
	        this.barcode = barcode;
	        this.state = state;
	        this.kbstate = kbstate;
		}
		@Override
		public void run() {
			if(null == DatabaseOper.con)
			{
				DatabaseOper.InitDatabaseConfig(ConfigPath);
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(ERROR_NOEXIT, "数据库连接失败", 0);
					return;
				}else SendDataMessage(CONNECT_SUCCESS, "数据库连接成功，正在扫描条码...", 0);
			}
			if(state.equals("2")){
				List<GWInfo> list = DatabaseOper.CXGWInfo(barcode);
				if(null == list){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, "查询异常", 0);
				}else{
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
					SendDataMessage(CX_SUCCESS, list, 0);
				}
			}else{
				int xh =DatabaseOper.ScanSMTInfo(barcode, "1", state, kbstate);
				if(100 != xh){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, DatabaseOper.ScanResult, 0);
				}
				else{
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
					SendDataMessage(SCANBARCODE_SUCCESS, DatabaseOper.ScanResult, 0);
				}
			}
			
		}
		
	}
	
	private static class GWHolder {
        TextView number;
        TextView tm;
        TextView jlrq;
        TextView zlbz;
        TextView kbbz;
        TextView jlry;
	}
	
	private class GWAdapt extends BaseAdapter{
		Context context;
		List<GWInfo> list;
		
		GWAdapt(Context context, List<GWInfo> List){
			this.context = context;
			this.list = List;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.gwitem, null);
			GWHolder holder = new GWHolder();
			holder.number = (TextView) view.findViewById(R.id.textItem_number);
			holder.tm = (TextView) view.findViewById(R.id.textItem_tm);
			holder.jlrq = (TextView) view.findViewById(R.id.textItem_jlrq);
			holder.zlbz = (TextView) view.findViewById(R.id.textItem_zlbz);
			holder.kbbz = (TextView) view.findViewById(R.id.textItem_kbbz);
			holder.jlry = (TextView) view.findViewById(R.id.textItem_jlry);
			int number = list.get(position).Getnumber();
			String tm = list.get(position).Getbarcode();
			String jlrq = list.get(position).Getrq();
			String zlbz = list.get(position).Getzlzt();
			String kbbz = list.get(position).Getkbzt();
			String jlry = list.get(position).Getry();
			
			if(0 == number) {
				view.setClickable(false);
				holder.number .setText("序 号");
			}
			else {
				holder.number .setText(""+number);
				holder.tm .setText(tm);
				holder.jlrq .setText(jlrq);
				if("0".equals(zlbz)) holder.zlbz .setText("张力NG");
				else if("1".equals(zlbz)) holder.zlbz .setText("张力OK");
				else holder.zlbz .setText("未知状态");
				
				if("0".equals(kbbz)) holder.kbbz .setText("孔壁NG");
				else if("1".equals(kbbz)) holder.kbbz .setText("孔壁OK");
				else holder.kbbz .setText("未知状态");
				holder.jlry .setText(jlry);
			}
			
			return view;
		}
		
	}
}
