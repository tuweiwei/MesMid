package com.yf.mesmid.activity;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yf.mesmid.db.DatabaseOper;
import com.yf.mesmid.R;
import com.yf.mesmid.util.ScanSound;
import com.yf.mesmid.entity.XGInfo;

public class XGActivity extends Activity{
	private EditText EditXG;
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
	
	private RadioButton RBXRadio;
	private RadioButton HWRadio;
	private RadioButton JQJBRadio;
	private RadioButton RGJBRadio;
	private RadioButton CXRadio;
	
	private ListView mlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
				if(arg1) Toast.makeText(XGActivity.this, "ɨ�蹤�ż������ѯȫ����Ϣ", 4000).show();
				
			}
		});
		TextOpertips = (TextView) findViewById(R.id.Text_xginfo);
		mlist = (ListView) findViewById(R.id.listView_xg);
		EditXG = (EditText) findViewById(R.id.edit_xg);
		//EditSop.setText("S1311000124");
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
					mDialog.setMessage("����ɨ������...");
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
		build.setTitle("�쳣��ʾ                              \n\n\n\n");
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
		SendDataMessage(ERRORDIALOG_CANCEL, "����Ի�����ʧ", 3);
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
			/*��ȡSOP�������*/
			else if(Code == SCANBARCODE_SUCCESS){
				String strResult = (String)msg.obj;
				mDialog.setMessage(strResult);
				mDialog.cancel();
				TextOpertips.setTextColor(Color.BLUE);
				TextOpertips.setText(strResult);
			}
			/*��ѯ�ɹ�*/
			else if(Code == CX_SUCCESS){
				mDialog.cancel();
				TextOpertips.setTextColor(Color.GREEN);
				TextOpertips.setText("��ѯ�ɹ�");
				List<XGInfo> list = (List<XGInfo>) msg.obj;
				XGAdapt adapt = new XGAdapt(getApplicationContext(), list);
				mlist.setAdapter(adapt);
			}
			else if(ERRORDIALOG_CANCEL == Code){
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
			if(null == DatabaseOper.con)
			{
				DatabaseOper.InitDatabaseConfig(ConfigPath);
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(ERROR_NOEXIT, "���ݿ�����ʧ��", 0);
					return;
				}else SendDataMessage(CONNECT_SUCCESS, "���ݿ����ӳɹ�������ɨ������...", 0);
			}
			if(state.equals("5")){
				List<XGInfo> list = DatabaseOper.CXXGInfo(barcode);
				if(null == list){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, "��ѯ�쳣", 0);
				}else{
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
					SendDataMessage(CX_SUCCESS, list, 0);
				}
			}else{
				int xh =DatabaseOper.ScanSMTInfo(barcode, "0", state, "0");
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
	
	private static class XGHolder {
        TextView number;
        TextView tm;
        TextView jlrq;
        TextView ztbz;
        TextView jlry;
	}
	
	private class XGAdapt extends BaseAdapter{
		Context context;
		List<XGInfo> list;
		
		XGAdapt(Context context, List<XGInfo> List){
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
			View view = inflater.inflate(R.layout.xgitem, null);
			XGHolder holder = new XGHolder();
			holder.number = (TextView) view.findViewById(R.id.textItem_number);
			holder.tm = (TextView) view.findViewById(R.id.textItem_tm);
			holder.jlrq = (TextView) view.findViewById(R.id.textItem_jlrq);
			holder.ztbz = (TextView) view.findViewById(R.id.textItem_ztbz);
			holder.jlry = (TextView) view.findViewById(R.id.textItem_jlry);
			int number = list.get(position).getNumber();
			String tm = list.get(position).getBarcode();
			String jlrq = list.get(position).getRq();
			String ztbz = list.get(position).getZt();
			String jlry = list.get(position).getRy();
			
			if(0 == number) {
				view.setClickable(false);
				holder.number .setText("�� ��");
			}
			else {
				holder.number .setText(""+number);
				holder.tm .setText(tm);
				holder.jlrq .setText(jlrq);
				if("1".equals(ztbz)) holder.ztbz .setText("�����");
				else if("2".equals(ztbz)) holder.ztbz .setText("����");
				else if("3".equals(ztbz)) holder.ztbz .setText("��������");
				else if("4".equals(ztbz)) holder.ztbz .setText("�˹�����");
				else holder.ztbz .setText("δ֪״̬");
				holder.jlry .setText(jlry);
			}
			
			return view;
		}
		
	}
}
