package com.yf.mesmid;

import java.util.ArrayList;
import java.util.List;

import com.yf.mesmid.XGActivity.RScan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WXActivity extends Activity{
	private EditText edit_tm;
	private EditText edit_mo;
	private EditText edit_zwxx;
	private EditText edit_blxx;
	private EditText edit_wxtm;
	private EditText edit_wxyy;
	private EditText edit_cstm;
	private EditText edit_wxcs;
	private EditText edit_wxr;
	private EditText edit_wxrxx;
	
	private EditText edit_sc;
	//private EditText edit_wxcs;
	//private Spinner spin_ndxs;
	//private Spinner spin_zrbm;
	
	private String mblxxtm; 
	
	private Button btn_save;
	private ListView WXList;
	
	private boolean bEnter = false;
	private ProgressDialog mDialog;
	private AlertDialog mErrorDialog;
	private final String ConfigPath = Environment.getExternalStorageDirectory().getPath()+"/MESConfig.ini";
	
	private final int SCANSN= 0x10;
	private final int SCANYYBARCODE= 0x20;
	private final int SCANCSBARCODE= 0x25;
	private final int SCANRYBARCODE= 0x26;
	private final int WXDR= 0x30;
	
	private final int ERROR_NOEXIT = 100;
	private final int CONNECT_SUCCESS = 101;
	private final int SCANSN_SUCCESS = 102;
	private final int SCANYYBARCODE_SUCCESS = 103;
	private final int SCANCSBARCODE_SUCCESS = 104;
	private final int SCANRYBARCODE_SUCCESS = 105;
	
	private final int WXDR_SUCCESS = 106;
	private final int ERRORDIALOG_CANCEL = 120;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wx2);
		//List<String> list = ParamBarcode("vdfhd:ghgh:dfhdfh:dfhdfh:fdhfd");
		mDialog = new ProgressDialog(this);
		edit_tm = (EditText) findViewById(R.id.edit_tm);
		edit_tm.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						edit_tm.setText(strBuff);
						edit_tm.setSelection(1);	
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
					mDialog.setMessage("正在扫描条码...");
					mDialog.show();
					RScan rScan = new RScan(strBuff, SCANSN, 0);
					Thread thread = new Thread(rScan);
					thread.start();
					
					edit_tm.setText(str.substring(0, Len-1));
					edit_tm.setSelection(Len-1);
					bEnter = true;
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		edit_mo = (EditText) findViewById(R.id.edit_mo);
		edit_mo.setEnabled(false);
		edit_zwxx = (EditText) findViewById(R.id.edit_zwxx);
		edit_zwxx.setEnabled(false);
		edit_blxx = (EditText) findViewById(R.id.edit_blxx);
		edit_blxx.setEnabled(false);
		
		edit_wxtm = (EditText) findViewById(R.id.edit_wxtm);
		edit_wxtm.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						edit_wxtm.setText(strBuff);
						edit_wxtm.setSelection(1);	
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
					mDialog.setMessage("正在扫描条码...");
					mDialog.show();
					RScan rScan = new RScan(strBuff, SCANYYBARCODE, 1);
					Thread thread = new Thread(rScan);
					thread.start();
					
					edit_wxtm.setText(str.substring(0, Len-1));
					edit_wxtm.setSelection(Len-1);
					bEnter = true;
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		edit_wxyy = (EditText) findViewById(R.id.edit_wxyy);
		edit_wxyy.setEnabled(false);
		edit_cstm = (EditText) findViewById(R.id.edit_cstm);
		//edit_cstm.requestFocus();
		edit_cstm.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						edit_cstm.setText(strBuff);
						edit_cstm.setSelection(1);	
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
					mDialog.setMessage("正在扫描条码...");
					mDialog.show();
					RScan rScan = new RScan(strBuff, SCANCSBARCODE, 2);
					Thread thread = new Thread(rScan);
					thread.start();
					
					edit_cstm.setText(str.substring(0, Len-1));
					edit_cstm.setSelection(Len-1);
					bEnter = true;
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		edit_wxcs = (EditText) findViewById(R.id.edit_wxcs);
		edit_wxcs.setEnabled(false);
		
		edit_wxr = (EditText) findViewById(R.id.edit_wxr);
		edit_wxr.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						edit_wxr.setText(strBuff);
						edit_wxr.setSelection(1);	
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
					mDialog.setMessage("正在扫描条码...");
					mDialog.show();
					RScan rScan = new RScan(strBuff, SCANRYBARCODE, 3);
					Thread thread = new Thread(rScan);
					thread.start();
					
					edit_wxr.setText(str.substring(0, Len-1));
					edit_wxr.setSelection(Len-1);
					bEnter = true;
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		edit_wxrxx = (EditText) findViewById(R.id.edit_wxrxx);
		edit_wxrxx.setFocusable(false);
		
		edit_sc = (EditText) findViewById(R.id.edit_sc);
		edit_sc.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						edit_sc.setText(strBuff);
						edit_sc.setSelection(1);	
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
					
					if("sssssjjjjjj".equals(strBuff)){
						
						String strtm = edit_tm.getEditableText().toString();
						if("".equals(strtm)){
							//Toast.makeText(WXActivity.this, "请先扫描流水号!", 4000).show();
							TipError("请先扫描流水号!", false);
							ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
							edit_tm.requestFocus();
							edit_sc.setText("");
							edit_sc.setSelection(0);
							bEnter = true;
							return;
						}
						//String strblxx = edit_blxx.getEditableText().toString();
						String strblxx = edit_blxx.getEditableText().toString();
						if("".equals(strblxx) || null == mblxxtm || "".equals(strblxx) || "".equals(mblxxtm)){
							//Toast.makeText(WXActivity.this, "线上不良条码不存在!", 4000).show();
							TipError("线上不良条码不存在, 请重新扫描流水号!", false);
							ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
							edit_tm.requestFocus();
							edit_sc.setText("");
							edit_sc.setSelection(0);
							bEnter = true;
							return;
						}else strblxx = mblxxtm;
						String strwxtm = edit_wxtm.getEditableText().toString();
						if("".equals(strtm) || "".equals(edit_wxyy.getEditableText().toString())){
							//Toast.makeText(WXActivity.this, "请先扫描不良原因!", 4000).show();
							TipError("请先扫描不良原因!", false);
							ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
							edit_wxtm.requestFocus();
							edit_sc.setText("");
							edit_sc.setSelection(0);
							bEnter = true;
							return;
						}
						String strcstm = edit_cstm.getEditableText().toString();
						if("".equals(strcstm) || "".equals(edit_wxcs.getEditableText().toString())){
							//Toast.makeText(WXActivity.this, "请先扫描维修措施号!", 4000).show();
							TipError("请先扫描维修措施号!", false);
							ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
							edit_cstm.requestFocus();
							edit_sc.setText("");
							edit_sc.setSelection(0);
							bEnter = true;
							return;
						}
						String strwxr = edit_wxr.getEditableText().toString();
						if("".equals(strcstm) || "".equals(edit_wxrxx.getEditableText().toString())){
							//Toast.makeText(WXActivity.this, "请先扫描维修人!", 4000).show();
							TipError("请先扫描维修人!", false);
							ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
							edit_wxr.requestFocus();
							edit_sc.setText("");
							edit_sc.setSelection(0);
							bEnter = true;
							return;
						}
						mDialog.setMessage("正在扫描条码...");
						mDialog.show();
						RScan rScan = new RScan(strtm+":"+strblxx+":"+strwxtm+":"+strwxr+":"+strcstm, WXDR, 0);
						Thread thread = new Thread(rScan);
						thread.start();
						
					}
					else{
						//Toast.makeText(WXActivity.this, "上传条码扫描失败!", 4000).show();
						TipError("请确认上传条码是否正确!", false);
						ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					}
					
					edit_sc.setText(str.substring(0, Len-1));
					edit_sc.setSelection(Len-1);
					bEnter = true;
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		/*edit_yj = (EditText) findViewById(R.id.edit_yj);
		edit_ej = (EditText) findViewById(R.id.edit_ej);
		edit_pcbabx = (EditText) findViewById(R.id.edit_pcbabx);
		edit_czry = (EditText) findViewById(R.id.edit_czry);
		edit_blyy = (EditText) findViewById(R.id.edit_blyy);
		edit_wxcs = (EditText) findViewById(R.id.edit_wxcs);*/
		
		/*spin_ndxs = (Spinner) findViewById(R.id.spin_ndxs);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.ndxs_arry, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spin_ndxs.setAdapter(adapter);
		
		spin_zrbm = (Spinner) findViewById(R.id.spin_zrbm);
		adapter = ArrayAdapter.createFromResource( this, R.array.zrbm_arry, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spin_zrbm.setAdapter(adapter);*/
		
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RScan rScan = new RScan("dgd:fdh:ytry:vbn:sadg", WXDR, 0);
				Thread thread = new Thread(rScan);
				thread.start();
				/*String strtm = edit_tm.getEditableText().toString();
				if("".equals(strtm)){
					Toast.makeText(WXActivity.this, "请先扫描流水号!", 4000).show();
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					edit_tm.requestFocus();
					return;
				}
				String strblxx = edit_blxx.getEditableText().toString();
				if("".equals(strblxx) || null == mblxxtm || "".equals(strblxx)){
					Toast.makeText(WXActivity.this, "线上不良条码不存在!", 4000).show();
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					edit_blxx.requestFocus();
					return;
				}else strblxx = mblxxtm;
				String strwxtm = edit_wxtm.getEditableText().toString();
				if("".equals(strtm) || "".equals(edit_wxyy.getEditableText().toString())){
					Toast.makeText(WXActivity.this, "请先扫描不良原因!", 4000).show();
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					edit_wxtm.requestFocus();
					return;
				}
				String strcstm = edit_cstm.getEditableText().toString();
				if("".equals(strcstm) || "".equals(edit_wxcs.getEditableText().toString())){
					Toast.makeText(WXActivity.this, "请先扫描维修措施号!", 4000).show();
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					edit_cstm.requestFocus();
					return;
				}
				String strwxr = edit_wxr.getEditableText().toString();
				if("".equals(strcstm) || "".equals(edit_wxrxx.getEditableText().toString())){
					Toast.makeText(WXActivity.this, "请先扫描维修人!", 4000).show();
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					edit_wxr.requestFocus();
					return;
				}
				RScan rScan = new RScan(strtm+":"+strblxx+":"+strwxtm+":"+strwxr+":"+strcstm, WXDR, 0);
				Thread thread = new Thread(rScan);
				thread.start();*/
				
			}
		});
		WXList= (ListView) findViewById(R.id.listView_wx);
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
			if(ERROR_NOEXIT == Code){
				mDialog.cancel();
				TipError((String)msg.obj, false);
			}
			else if(ERRORDIALOG_CANCEL == Code){
				mErrorDialog.cancel();
			}
			else if(SCANSN_SUCCESS == Code){
				mDialog.cancel();
				List<WXList> list = (List<WXList>) msg.obj;
				edit_mo.setText(list.get(1).Getmo());
				edit_zwxx.setText(list.get(1).Getzwxx());
				edit_blxx.setText(list.get(1).Getblxx());
				mblxxtm = list.get(1).Getbltm();
				WXAdapt adapt = new WXAdapt(getApplicationContext(), list);
				WXList.setAdapter(adapt);
							
				//edit_wxcs.setFocusable(true);
				if(!"此产品已维修".equals(list.get(1).Getblxx()) && !"此条码对应工单没有启动".equals(list.get(1).Getblxx())
						&& !"此条码未出现不良现象".equals(list.get(1).Getblxx())){
					edit_wxtm.requestFocus();
				}
				
			}
			else if(SCANYYBARCODE_SUCCESS == Code){
				mDialog.cancel();
				String wxxx = (String) msg.obj;
				edit_wxyy.setText(wxxx);
				
				//edit_cstm.setFocusable(true);
				edit_cstm.requestFocus();
			}
			else if(SCANCSBARCODE_SUCCESS == Code){
				mDialog.cancel();
				String wxxx = (String) msg.obj;
				edit_wxcs.setText(wxxx);
				
				//edit_wxr.setFocusable(true);
				edit_wxr.requestFocus();
			}
			else if(SCANRYBARCODE_SUCCESS == Code){
				mDialog.cancel();
				String wxry = (String) msg.obj;
				edit_wxrxx.setText(wxry);
				
				//edit_wxr.setFocusable(true);
				edit_sc.requestFocus();
			}
			else if(WXDR_SUCCESS == Code){
				Toast.makeText(getApplicationContext(), "上传成功", 4000).show();
				mDialog.cancel();
				
				bEnter = false;
				edit_tm.setText("");
				edit_mo.setText("");
				edit_zwxx.setText("");
				edit_blxx.setText("");
				edit_wxtm.setText("");
				edit_wxyy.setText("");
				edit_cstm.setText("");
				edit_wxcs.setText("");
				edit_wxr.setText("");
				edit_wxrxx.setText("");
				edit_sc.setText("");
				WXList.setAdapter(null);
				edit_tm.requestFocus();
				bEnter = true;
			}
		};
	};
	
	private void TipError(String strInfo, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(WXActivity.this);
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
	
	class RScan implements Runnable{
		private String barcode;
		private int mode;
		private int ztbz;
		RScan(String barcode, int mode, int ztbz)
		{
	        this.barcode = barcode;
	        this.mode = mode;
	        this.ztbz = ztbz;
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
			if(SCANSN == mode)
			{
				List<WXList> ListWX = DatabaseOper.ScanWXBarcode(barcode);
				if( null == ListWX || ListWX.size() < 2){
					SendDataMessage(ERROR_NOEXIT, "查询维修流水号失败", 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				}else{
					SendDataMessage(SCANSN_SUCCESS, ListWX, 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
				}
			}
			else if(SCANYYBARCODE == mode)
			{
				String wxxx = DatabaseOper.ScanEJBarcode(barcode, ztbz);
				if(null == wxxx){
					SendDataMessage(ERROR_NOEXIT, "查询不良原因失败", 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				}else if(wxxx.equals("")){
					SendDataMessage(ERROR_NOEXIT, "不良原因条码未注册", 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				}
				else{
					SendDataMessage(SCANYYBARCODE_SUCCESS, wxxx, 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
				}
			}
			else if(SCANCSBARCODE == mode)
			{
				String wxxx = DatabaseOper.ScanEJBarcode(barcode, ztbz);
				if(null == wxxx){
					SendDataMessage(ERROR_NOEXIT, "查询维修措施失败", 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				}else if(wxxx.equals("")){
					SendDataMessage(ERROR_NOEXIT, "维修措施条码未注册", 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				}
				else{
					SendDataMessage(SCANCSBARCODE_SUCCESS, wxxx, 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
				}
			}
			else if(SCANRYBARCODE == mode)
			{
				String wxxx = DatabaseOper.ScanEJBarcode(barcode, ztbz);
				if(null == wxxx){
					SendDataMessage(ERROR_NOEXIT, "查询维修人失败", 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				}else if(wxxx.equals("")){
					SendDataMessage(ERROR_NOEXIT, "维修人条码未注册", 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				}else{
					SendDataMessage(SCANRYBARCODE_SUCCESS, wxxx, 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
				}
			}
			else if(WXDR == mode)
			{
				List<String> list = ParamBarcode(barcode);
				String strjg = DatabaseOper.DRWXInfo(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
				if(null == strjg || ! strjg.equals("100")){
					SendDataMessage(ERROR_NOEXIT, "导入维修信息失败", 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				}else{
					SendDataMessage(WXDR_SUCCESS, strjg, 0);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
				}
			}
		}
		
	}
	
	List<String> ParamBarcode(String barcode)
	{
		List<String> list = new ArrayList<String>();
		int index = barcode.indexOf(":");
		String sn = barcode.substring(0, index);
		list.add(sn);
		int end = barcode.indexOf(":", index+1);
		String xsbltm = barcode.substring(index+1, end);
		list.add(xsbltm);
		index = end;
		end = barcode.indexOf(":", index+1);
		String bltm = barcode.substring(index+1, end);
		list.add(bltm);
		index = end;
		end = barcode.indexOf(":", index+1);
		String czry = barcode.substring(index+1, end);
		list.add(czry);
		index = end;
		String wxcs = barcode.substring(index+1);
		list.add(wxcs);
		return list;	
	}
	
	private static class WXHolder {
		TextView number;
        TextView wxyy;
        TextView wxcs;
        TextView wxry;
        TextView wxsj;
	}
	
	private class WXAdapt extends BaseAdapter{
		Context context;
		List<WXList> list;
		
		WXAdapt(Context context, List<WXList> List){
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
			View view = inflater.inflate(R.layout.wxitem, null);
			WXHolder holder = new WXHolder();
			holder.number = (TextView) view.findViewById(R.id.textItem_number);
			holder.wxyy = (TextView) view.findViewById(R.id.textItem_wxyy);
			holder.wxcs = (TextView) view.findViewById(R.id.textItem_wxcs);
			holder.wxry = (TextView) view.findViewById(R.id.textItem_wxry);
			holder.wxsj = (TextView) view.findViewById(R.id.textItem_wxsj);
			int number = list.get(position).Getnumber();
			String wxyy = list.get(position).Getwxyy();
			String wxcs = list.get(position).Getwxcs();
			String wxry = list.get(position).Getryxx();
			String wxsj = list.get(position).Getwxsj();
			
			if(0 == number) {
				view.setClickable(false);
				holder.number .setText("序 号");
			}
			else {
				holder.wxyy .setText(wxyy);
				holder.wxcs .setText(wxcs);
				holder.wxry .setText(wxry);
				holder.wxsj .setText(wxsj);
				if(null != wxyy && null != wxcs
						&& null != wxry && null != wxsj
						&& !wxyy.equals("") && !wxcs.equals("")
						&& !wxry.equals("") && !wxsj.equals("")){
					holder.number .setText(""+number);
				}
				else holder.number .setText("");
			}
			
			return view;
		}
		
	}
}
