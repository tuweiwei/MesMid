package com.yf.mesmid;

import java.io.FileDescriptor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yf.mesmid.R.array;
import com.yf.mesmid.SopActivity.RScan;
import com.yf.mesupdate.MyApp;
import com.yf.mesupdate.NotificationUpdateActivity;
import com.yf.mesupdate.UpdataInfo;
import com.yf.mesupdate.GetUpdataInfo;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JobListActivity  extends Activity{
	private final String TAG = "JobList";
	private TextView text_itemname=null;
	private ListView ListJobOrder;
	private int iSelJob = 0;
	private ListView ListProcedure;
	private int iSelProcedure = 0;
	private ListView ListMaterial;
	private int iSelMaterial = 0;
	private Button BtnSave;
	private Button BtnRequery;
	private Button BtnYuLan;
	private ProgressDialog mDialog;
	private AlertDialog mErrorDialog;
	private EditText EditInput;
	private boolean bEnter = false;
	private String StringInput = null;
	private String StringMaterial = null;
	
	private List<JobOrder> mListJobOrder = new ArrayList<JobOrder>();
	private List<Procedure> mListProcedure = new ArrayList<Procedure>();
	private List<Material> mListMaterial = new ArrayList<Material>();
	
	private final String ConfigPath = Environment.getExternalStorageDirectory().getPath()+"/MESConfig.ini";
	private final int GET_UPDATEINFO = 1;
	private final int QUERY_JOBORDER = 2;
	private final int QUERY_PROCEDURE = 3;
	private final int QUERY_MATERIAL = 4;
	private final int SCAN_BATCHCODE = 5;
	private final int QUERY_SOPINFO = 6;
	
	private final int QUERY_DEVICEID = 100;
	private final int ERROR_EXIT = 101;
	private final int CONNECT_SUCCESS = 102;
	private final int GETJOBORDER_SUCCESS = 103;
	private final int GETPROCEDURE_SUCCESS = 104;
	private final int GETMATERIAL_SUCCESS = 105;
	private final int SCANBATCHCODE_SUCCESS = 106;
	private final int GETSOPINFO_SUCCESS = 107;
	private final int ERROR_NOEXIT= 108;
	private final int PROCEDURE_COLORCHANGE= 109;
	
	private final int ERRORDIALOG_CANCEL = 120;
	private final int UPDATE = 130;
	private final int UPDATE_ERROR = 131;
	
	private boolean mbSelMateria = true;
	private final int SelColor = Color.GREEN;
	private final int SelWrongColor = Color.RED;
	private final int SelTextColor = Color.YELLOW;
	private final int SelBackgroundColor = Color.BLUE;
	private final int CancelSelTextColor = Color.BLACK;
	private final int CancelSelBackgroundColor = Color.TRANSPARENT;
	private final int NONeedSelBackgroundColor = Color.GRAY;
	
	private final int NEEDPCGKBackgroundColor = Color.BLUE;
	private final int NONEEDPCGKBackgroundColor = Color.WHITE;
	private final int SCANNEEDPCGKBackgroundColor = Color.BLUE;
	
	private final String STRING_UPDATE = "APP有更新";
	private final String STRING_NOUPDATE = "APP无更新";
	
	//private String IP = "192.168.1.114";
	private String DeviceID = null;
	private WifiManager Wifimanager;
	private WIFIBroadcastReceiver WifiReceiver;
	
	private MyApp app;
	private int currentVersionCode;
	private String curversion;
	private UpdataInfo info;
	private TextView tv_splash_version;
	private LinearLayout ll_splash_main;
	//----点检APK进入
	private boolean djjr = false;
	private int gdxh = 0;
	private int gxxh = 0;
	private String user = "";
	//----料号扫描结果
	private RelativeLayout RelativeBarcodeResult;
	private TextView TextBarcodeResult;
	private Button BtnCancle;
	//----小键盘输入
	private final String KEYBOARD_MSG="mes.keyboardmsg";
	private final int  DELETE_KEY=101;
	private final int ENTER_KEY=102;
	private Button BtnKeyBoard;
	private List<Integer> ListKeyBoard = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.joblist);
		//ConfigurationFile.SqlLog(Environment.getExternalStorageDirectory().getPath()+"/sqllog.txt", "bbbb");
		app = (MyApp) getApplication();
		curversion = getVersion();
		text_itemname = (TextView) findViewById(R.id.text_itemname);
		if(DatabaseOper.SC_MODE==DatabaseOper.ZC_MODE) text_itemname.setText("JobOrder 正常生产");
		else if(DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE) text_itemname.setText("OQC 正常生产");
		else if(DatabaseOper.SC_MODE==DatabaseOper.FG_MODE) text_itemname.setText("JobOrder 线内返工");
		//CheckUpdate();
		//----料号扫描结果
		RelativeBarcodeResult = (RelativeLayout) findViewById(R.id.Relative_barcoderesult);
		TextBarcodeResult = (TextView) findViewById(R.id.Text_barcoderesult);
		BtnCancle = (Button) findViewById(R.id.btn_cancel);
		BtnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ListJobOrder.setVisibility(View.VISIBLE);
				//ListProcedure.setVisibility(View.VISIBLE);
				RelativeBarcodeResult.setVisibility(View.GONE);
				
			}
		});
		//--点检进入
		djjr = getIntent().getBooleanExtra("djjr", false);
		Log.i("JobList Main", ""+djjr);
		if(djjr){
			gdxh = getIntent().getIntExtra("gdxh", 0);
			iSelJob = gdxh;
			gxxh = getIntent().getIntExtra("gxxh", 0);
			iSelProcedure = gxxh;
			user = getIntent().getStringExtra("user");
			Log.i("JobList Main", ""+gdxh);
			Log.i("JobList Main", ""+gxxh);
			Log.i("JobList Main", ""+user);
		}
		BtnSave = (Button) findViewById(R.id.btn_save);
		BtnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//-----------------------------------------扫描必须扫描料号完成才能进入SOP
				if(DatabaseOper.mBarcodeNums == DatabaseOper.mNeedScanBarcodeNums 
						&& DatabaseOper.mBatchNums == DatabaseOper.mNeedScanBatchNums) {
					MesLsData.bYuLang = false;
					mDialog.setMessage("正在查询SOP...");
					mDialog.show();
					ThreadData thread = new ThreadData(JobListActivity.this, QUERY_SOPINFO);
					thread.start();
				}
				else {
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					TipError("\n请先扫描完料号及批次\n", 2, false);
				}
				//-----------------------------------------
				/*mDialog.setMessage("正在查询SOP...");
				mDialog.show();
				ThreadData thread = new ThreadData(JobListActivity.this, QUERY_SOPINFO);
				thread.start();*/
			}
		});
		BtnSave.setEnabled(false);
		BtnSave.setFocusable(false);
		
		BtnRequery = (Button) findViewById(R.id.btn_requery);
		BtnRequery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				djjr = false;
				DatabaseOper.GX_MODE=DatabaseOper.ZC_MODE;
				EditInput.setEnabled(false);
				BtnKeyBoard.setEnabled(true);
				if(DatabaseOper.ConnMode.equals(DatabaseOper.WIRE_CONN)){
					if(null != DatabaseOper.con){
						try {
							DatabaseOper.con.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						DatabaseOper.con = null;
					}
					iSelJob = 0;
					mDialog.setMessage("正在重新查询...");
					mDialog.show();
					ListJobOrder.setAdapter(null);
					ListMaterial.setAdapter(null);
					ListProcedure.setAdapter(null);
					ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
					thread.start();
					BtnSave.setEnabled(false);
					BtnYuLan.setEnabled(false);
				}
				else{
					mDialog.setMessage("正在检查WIFI网络...");
					mDialog.show();
					Intent intent = new Intent(JobListActivity.this, WifiService.class);
					intent.putExtra("repeatquery", true);
					startService(intent);
				}
				
			}
		});
		BtnYuLan = (Button) findViewById(R.id.btn_yulan);
		BtnYuLan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				/*后门，扫描预览条码(YULANGSOP),可预览SOP5分钟*/
				MesLsData.bYuLang = true;
				mDialog.setMessage("正在查询SOP...");
				mDialog.show();
				ThreadData thread = new ThreadData(JobListActivity.this, QUERY_SOPINFO);
				thread.start();
				
			}
		});
		BtnYuLan.setEnabled(false);
		BtnYuLan.setFocusable(false);
		EditInput = (EditText) findViewById(R.id.edit_input);
		EditInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = s.toString();
				int Len = str.length();
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						EditInput.setText(strBuff);
						EditInput.setSelection(1);	
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
					//Toast.makeText(MainActivity.this, strBuff, 2000).show();
					//mbarcode = strBuff;
					EditInput.setText(strBuff);
					EditInput.setSelection(strBuff.length());
					bEnter = true;
					if(strBuff.equals(MesLsData.QUIT_BARCODE)){
						finish();
						System.exit(0);	
					}
					//---小键盘模糊查询
					if(DatabaseOper.GX_MODE==DatabaseOper.KEYBOARD_MODE){
						List<Integer> list =GetJobOrderContainList(mListJobOrder, strBuff);
						int iCount=list.get(0);
						if(iCount<=0) {
							DatabaseOper.GX_MODE=DatabaseOper.ZC_MODE;
							Toast.makeText(getApplicationContext(), "未能查询到匹配工单", 3000).show();
						}
						/*模糊查询到一个工单则可直接查工序*/
						else if(iCount==1){
							gdxh=list.get(1);
							iSelJob=gdxh;
							if(DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE) iSelProcedure =1;
							//djjr=true;
							DatabaseOper.GX_MODE=DatabaseOper.KEYBOARD_MODE;
							mDialog.setMessage("正在查询订单数据...");
							ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
							thread.start();
						}
						/*模糊查询到多个工单, 则直接显示多工单*/
						else if(iCount>=2){
							list.remove(0);
							ListKeyBoard=list;
							DatabaseOper.GX_MODE=DatabaseOper.KEYBOARD_DISPLAYGD;
							mDialog.setMessage("正在查询订单数据...");
							ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
							thread.start();
						}
						else return;
						/*for(int i = 0; i < mListJobOrder.size();i++)
						{
							String zzdh=mListJobOrder.get(i).Getjoborder();
							if(zzdh !=null && zzdh.length()>=1){
								if(zzdh.contains(strBuff)){
									gdxh=i;
									iSelJob=gdxh;
									if(DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE) iSelProcedure =1;
									//djjr=true;
									DatabaseOper.GX_MODE=DatabaseOper.KEYBOARD_MODE;
									mDialog.setMessage("正在查询订单数据...");
									ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
									thread.start();
								}
							}
						}*/
					}
					/*后门，不扫描料号强制进入SOP*/
					if(strBuff.equals(MesLsData.QIANGZHI_BARCODE) && BtnSave.isEnabled()){
						MesLsData.bYuLang = false;
						mDialog.setMessage("正在查询SOP...");
						mDialog.show();
						ThreadData thread = new ThreadData(JobListActivity.this, QUERY_SOPINFO);
						thread.start();
						return;
					}
					/*后门，扫描预览条码(YULANGSOP),可预览SOP5分钟*/
					if(strBuff.equals(MesLsData.YULANGSOP_BARCODE)){
						MesLsData.bYuLang = true;
						mDialog.setMessage("正在查询SOP...");
						mDialog.show();
						ThreadData thread = new ThreadData(JobListActivity.this, QUERY_SOPINFO);
						thread.start();
						return;
					}
					
					//boolean bRet = false;
					/*for(int i = 0; i < mListMaterial.size(); i++)
					{
						Material material = mListMaterial.get(i);
						if(!strBuff.equals(material.Getmaterial())){
							View view = ListMaterial.getChildAt(iSelMaterial);
							view.setBackgroundColor(Color.TRANSPARENT);
							view = ListMaterial.getChildAt(i);
							view.setBackgroundColor(SelColor);
							EditText edittext = (EditText) view.findViewById(R.id.editItem_batchcode);
							edittext.setText(strBuff);
							//edittext.setBackgroundColor(Color.GRAY);
							iSelMaterial = i;
							return;
						}
					}*/
					if(mListMaterial.size() > 1){
						mDialog.setMessage("正在扫描条码...");
						mDialog.show();	
						//mListMaterial.get(iSelMaterial).Setbatchcode(strBuff);
						StringInput = strBuff;
						//iSelMaterial = mListMaterial.
						ThreadData thread = new ThreadData(JobListActivity.this, SCAN_BATCHCODE);
						thread.start();
					}
					
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
        EditInput.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int Len = EditInput.getEditableText().toString().length();
				EditInput.setSelection(Len);
			}
		});
        EditInput.setClickable(false);
        EditInput.setSelected(false);
        EditInput.setSelectAllOnFocus(false);
        EditInput.setEnabled(false);
		BtnRequery.setFocusable(false);
		ListJobOrder = (ListView) findViewById(R.id.listView_joborder);
		ListJobOrder.setFocusable(false);
		ListProcedure = (ListView) findViewById(R.id.listView_procedure);
		ListProcedure.setFocusable(false);
		ListMaterial = (ListView) findViewById(R.id.listView_material);
		ListMaterial.setFocusable(false);
		//JobOrder joborder = new JobOrder(i, "工 单 号", "机型代码", "生产批量", null);
		/*mListMaterial.add(new Material(0, "材    料"));
		for(int i = 1; i < 20; i++)
		{
			Material material = new Material(i, "螺丝钉");
			mListMaterial.add(material);
		}
		mListProcedure.add(new Procedure(0, "工    序", "名    称", null));
		for(int i = 1; i < 20; i++)
		{
			Procedure procedure = new Procedure(i, "PC6953MB5003", "扫描平板", null);
			mListProcedure.add(procedure);
		}
		mListJobOrder.add(new JobOrder(0, "工 单 号", "机型代码", "生产批量", null));
		for(int i = 1; i < 20; i++)
		{
			JobOrder joborder = new JobOrder(i, "7400001113", "CP6953-KA-MB0-V5", "100", null);
			mListJobOrder.add(joborder);
		}*/
		//JobOrderAdapt jobadapt = new JobOrderAdapt(this, mListJobOrder);
		//ListJobOrder.setAdapter(jobadapt);
		ListJobOrder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(0 == arg2) return;
				iSelProcedure = 0;
				iSelJob = arg2;
				//若点击了则不为点检模式
				djjr = false;
				//DatabaseOper.GX_MODE=DatabaseOper.ZC_MODE;
				//arg0.setBackgroundColor(Color.WHITE);
				ListView listview = (ListView)arg0;
				JobOrderAdapt adapt = (JobOrderAdapt) listview.getAdapter();
				adapt.notifyDataSetChanged();
				EditInput.setEnabled(false);
				//View view = listview.getChildAt(iSelJob);
				//view.setBackgroundColor(Color.TRANSPARENT);
				//SetSelColor(view, false);
				//JobOrderAdapt jobadapt = new JobOrderAdapt(JobListActivity.this, mListJobOrder);
				//ListJobOrder.setAdapter(jobadapt);
				//listview.geti
				
				//arg1.setBackgroundColor(SelColor);
				//SetSelColor(arg1, true);
				mDialog.setMessage("正在查询工序名称...");
				mDialog.show();
				ListMaterial.setAdapter(null);
				ListProcedure.setAdapter(null);
				ThreadData thread = new ThreadData(JobListActivity.this, QUERY_PROCEDURE);
				thread.start();
				/*ProcedureAdapt procedureadapt = new ProcedureAdapt(JobListActivity.this, mListProcedure);
				ListProcedure.setAdapter(procedureadapt);*/
				BtnSave.setEnabled(false);
				BtnYuLan.setEnabled(false);
				Log.i(TAG, "start query procedure");
				
			}
		});
		ListProcedure.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(0 == arg2) return;
				DatabaseOper.mBarcodeNums = 0;
				DatabaseOper.mNeedScanBarcodeNums=0;			
				DatabaseOper.mBatchNums = 0;
				DatabaseOper.mNeedScanBatchNums=0;
				iSelMaterial = 0;
				iSelProcedure = arg2;
				//若点击了则不为点检模式
				djjr = false;
				//DatabaseOper.GX_MODE=DatabaseOper.ZC_MODE;
				//------------
				ListView listview = (ListView)arg0;
				//Parcelable state = ListProcedure.onSaveInstanceState();//保存ListProcedure数据
				ProcedureAdapt adapt = (ProcedureAdapt) listview.getAdapter();
				adapt.notifyDataSetChanged();
				//mProcedureAdapt.notifyDataSetChanged();
				//ListProcedure.setAdapter(adapt);
				
				//ListProcedure.onRestoreInstanceState(state);//保存数据还原
				//View view = listview.getChildAt(iSelProcedure);
				//View view = listview.getAdapter().getView(iSelProcedure,null,arg0);
				//view.setBackgroundColor(Color.TRANSPARENT);
				//arg1.setBackgroundColor(SelColor);
				
				/*MaterialAdapt materiaadapt = new MaterialAdapt(JobListActivity.this, mListMaterial);
				ListMaterial.setAdapter(materiaadapt);*/
				mDialog.setMessage("正在查询材料名称...");
				mDialog.show();
				ListMaterial.setAdapter(null);//ListMaterial.set
				MesLsData.lcldm.clear();//材料条码清除
				ThreadData thread = new ThreadData(JobListActivity.this, QUERY_MATERIAL);
				thread.start();
				Log.i(TAG, "start query materia");
				
			}
		});
		ListMaterial.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(0 == arg2) return;
				mbSelMateria = true;
				iSelMaterial = arg2;
				ListView listview = (ListView)arg0;
				MaterialAdapt adapt = (MaterialAdapt) listview.getAdapter();
				adapt.notifyDataSetChanged();
				//View view = listview.getChildAt(iSelMaterial);
				//view.setBackgroundColor(Color.TRANSPARENT);
				
				//arg1.setBackgroundColor(SelColor);
				//EditText edittext = (EditText) view.findViewById(R.id.editItem_batchcode);
				//edittext.setText("");
				//edittext.setEnabled(true);
				//edittext.setFocusable(true);
				
				//Log.i(TAG, "start edit batchcode");
				//Toast.makeText(getApplicationContext(), "ListMaterial", 2000).show();
			}
		});
		Log.i(TAG, "start query joborder");
		Wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("正在获取设备ID...");
		mDialog.show();
		DeviceID = getMyUUID();
		//DeviceID="50524553542400A2";
		//DeviceID="5052455354240010";
		if(null == DeviceID){
			SendDataMessage(QUERY_DEVICEID, "正在获取设备ID...", 1);
		}
		else{
			mDialog.setMessage("正在检查WIFI网络...");
			//mDialog.setMessage("正在连接数据库...");
			//ThreadData thread = new ThreadData(this, GET_UPDATEINFO);
			//thread.start();
		}
		DatabaseOper.InitDatabaseConfig(ConfigPath);
		//这已不是主界面
		WifiReceiver = new WIFIBroadcastReceiver();
		IntentFilter Wififilter = new IntentFilter();
		Wififilter.addAction(DatabaseOper.FirstWIFI_MSG);
		Wififilter.addAction(DatabaseOper.RepeatWIFI_MSG);
		registerReceiver(WifiReceiver, Wififilter);
		/*Intent intent = new Intent(this, WifiService.class);
		intent.putExtra("repeatquery", false);
		startService(intent);*/
		//CountThread();
		mDialog.setMessage("正在查询订单数据...");
		ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
		thread.start();
		//----小键盘输入
		BtnKeyBoard=(Button) findViewById(R.id.btn_keyboard);
		BtnKeyBoard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				bEnter = false;
				EditInput.setText("");
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), KeyBoard.class);
				startActivity(intent);
				
			}
		});
		BtnKeyBoard.setEnabled(true);
		IntentFilter KeyBoardfilter = new IntentFilter();
		KeyBoardfilter.addAction(KEYBOARD_MSG);
		registerReceiver(KeyBoardReceiver, KeyBoardfilter);
	}
	
	private BroadcastReceiver KeyBoardReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String strAction = arg1.getAction();
			if(strAction.equals(KEYBOARD_MSG)){
				int key=arg1.getIntExtra("key", 0);
				if(key==DELETE_KEY){
					String str=EditInput.getText().toString();
					if(str.length()>0) str=str.substring(0, str.length()-1);
					EditInput.setText(str);
				}
				else if(key==ENTER_KEY){
					DatabaseOper.GX_MODE=DatabaseOper.KEYBOARD_MODE;
					String str=EditInput.getText().toString();
					EditInput.setText(str+"\n");
				}
				else{
					String str=EditInput.getText().toString();
					EditInput.setText(str+key);
					/*if(str.length()>=5) Toast.makeText(getApplicationContext(), "模糊查询不得超过5位数字", 3000).show();
					else EditInput.setText(str+key);*/
				}
				
			}
			
		}
	};
	
	//得到工单模糊查询匹配次数
	int GetJobOrderContainNums(List<JobOrder> list, String strContain){
		int count = 0;
		for(int i = 0; i < mListJobOrder.size();i++)
		{
			String zzdh=mListJobOrder.get(i).Getjoborder();
			if(zzdh !=null && zzdh.length()>=1){
				//zzdh=zzdh.substring(zzdh.length()-5, zzdh.length());
				if(zzdh.contains(strContain)){
					count++;
				}
			}
		}
		return count;
	}
	
	//得到工单模糊查询匹配数组
	List<Integer> GetJobOrderContainList(List<JobOrder> list, String strContain){
		List<Integer> listcontain = new ArrayList<Integer>();
		int count = 0;
		for(int i = 0; i < mListJobOrder.size();i++)
		{
			String zzdh=mListJobOrder.get(i).Getjoborder();
			if(zzdh !=null && zzdh.length()>=1){
				//zzdh=zzdh.substring(zzdh.length()-5, zzdh.length());
				if(zzdh.contains(strContain)){
					listcontain.add(i);
					count++;
				}
			}
		}
		listcontain.add(0, count);
		return listcontain;
	}
	
	void CountThread(){
		new Thread(){
			public void run() {
				int count = 0;
				while(true){
					Log.d("---", "count:" + count);
					if(count == 20){
						Log.d("---", "CONNECT_SUCCESS:" + count);
						finish();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					count++;
				}
			};
		}.start();
	}
	
	class WIFIBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(DatabaseOper.FirstWIFI_MSG)){
				mDialog.setMessage("正在连接数据库...");
				ThreadData thread = new ThreadData(getApplicationContext(), GET_UPDATEINFO);
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
				iSelJob = 0;
				mDialog.setMessage("正在重新查询...");
				mDialog.show();
				ListJobOrder.setAdapter(null);
				ListMaterial.setAdapter(null);
				ListProcedure.setAdapter(null);
				ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
				thread.start();
				BtnSave.setEnabled(false);
				BtnYuLan.setEnabled(false);
			}
			
		}
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		boolean bWifi = getIntent().getBooleanExtra("wifi", false);
		if(bWifi && null !=DeviceID) {
			mDialog.setMessage("正在连接数据库...");
			ThreadData thread = new ThreadData(this, GET_UPDATEINFO);
			thread.start();
		}

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		//2小时转线时
		/*Toast.makeText(this, "JobList", 4000).show();
		ListJobOrder.setVisibility(View.GONE);
		ListProcedure.setVisibility(View.GONE);*/
		//在SOP页面即可退出
		boolean bScanBatchCode = intent.getBooleanExtra("scanbatchcode", false);
		if(bScanBatchCode){
			ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_SCAN);
			TipScanBatchCodeEx("\n已到2小时，请扫描料号\n", false);
			//-----------------------------------------扫描料号完成才能进入SOP
			DatabaseOper.lBarcodes.clear();
			DatabaseOper.lBatchs.clear();
			DatabaseOper.mBarcodeNums = 0;
			DatabaseOper.mNeedScanBarcodeNums=0;
			DatabaseOper.mBatchNums = 0;
			DatabaseOper.mNeedScanBatchNums=0;
			mDialog.setMessage("正在查询材料名称...");
			mDialog.show();
			ListMaterial.setAdapter(null);
			MesLsData.lcldm.clear();//材料条码清除
			ThreadData thread = new ThreadData(JobListActivity.this, QUERY_MATERIAL);
			thread.start();
			Log.i(TAG, "start query materia");
			//-----------------------------------------
		}
		boolean bExit = intent.getBooleanExtra("exit", false);
		if(bExit) {
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
		super.onNewIntent(intent);
	}
	
	private void SetSelColor(View view, boolean bSel)
	{
		JobOrderHolder holder = new JobOrderHolder();
		holder.number = (TextView) view.findViewById(R.id.textItem_number);
		holder.producedata = (TextView) view.findViewById(R.id.textItem_producedata);
		holder.joborder = (TextView) view.findViewById(R.id.textItem_joborder);
		holder.materialorder = (TextView) view.findViewById(R.id.textItem_materialorder);
		holder.model = (TextView) view.findViewById(R.id.textItem_model);
		holder.batch = (TextView) view.findViewById(R.id.textItem_batch);
		int TextColor = 0, BackgroundColor = 0;
		if(bSel){
			TextColor = SelTextColor;
			BackgroundColor = SelBackgroundColor;
		}
		else{
			TextColor = CancelSelTextColor;
			BackgroundColor = CancelSelBackgroundColor;
		}
		holder.number.setBackgroundColor(BackgroundColor);
		holder.producedata.setBackgroundColor(BackgroundColor);
		holder.joborder.setBackgroundColor(BackgroundColor);
		holder.materialorder.setBackgroundColor(BackgroundColor);
		holder.model.setBackgroundColor(BackgroundColor);
		holder.batch.setBackgroundColor(BackgroundColor);
		
		holder.number.setTextColor(TextColor);
		holder.producedata.setTextColor(TextColor);
		holder.joborder.setTextColor(TextColor);
		holder.materialorder.setTextColor(TextColor);
		holder.model.setTextColor(TextColor);
		holder.batch.setTextColor(TextColor);
	}
	
	/**
	 * @return 获取当前应用程序的版本号
	 */
	private String getVersion() {
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {

			e.printStackTrace();
			return "版本号未知";
		}
	}
	
	void CheckUpdate(){
		Thread thread = new Thread(){
			@Override
			public void run() {
				UpdataInfo updatainfo = DatabaseOper.GetUpdateInfo();
				if(null == updatainfo){
					info = updatainfo;
					if(isNeedUpdate(curversion)){
						SendDataMessage(UPDATE, "APP有更新", 0);
					}
				}else{
					
				}
				super.run();
			}
		};
		thread.start();
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
				Intent it = new Intent(JobListActivity.this, NotificationUpdateActivity.class);
				it.putExtra("apkurl", info.getApkurl());
				startActivity(it);
//				MapApp.isDownload = true;
				//loadMainUI();
				//finish();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//loadMainUI();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private String getDeviceId() {
        String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        return android_id;
    }
	
	/*
	 * 得到wifi mac地址
	 * */
	private String getLocalMacAddress() {
		Wifimanager.setWifiEnabled(true);
		WifiInfo info = Wifimanager.getConnectionInfo();
		return info.getMacAddress();
	}
	
	private String getMyUUID(){
    	String UUID = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        
        String ret=(!TextUtils.isEmpty(UUID) ? UUID : null);
        return ret;
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null != DatabaseOper.con){
			try {
				DatabaseOper.con.close();
				DatabaseOper.con = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		unregisterReceiver(WifiReceiver);
		unregisterReceiver(KeyBoardReceiver);
		if(mDialog.isShowing()) {
			mDialog.cancel();
		}
		//Intent intent = new Intent(this, WifiService.class);
		//stopService(intent);
	}
	
	private void TipError(String strInfo, int time, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(JobListActivity.this);
		build.setTitle("异常提示");
		build.setMessage(strInfo);
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
		
		SendDataMessage(ERRORDIALOG_CANCEL, "错误对话框消失", time);
	}
	
	private void TipScanBatchCode(String strInfo, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(JobListActivity.this);
		build.setTitle("请扫描批次料号");
		build.setMessage(strInfo);
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
		
		SendDataMessage(ERRORDIALOG_CANCEL, "错误对话框消失", 8);
	}
	
	private void TipScanBatchCodeEx(String strInfo, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(JobListActivity.this);
		build.setTitle("请扫描批次料号");
		build.setMessage(strInfo);
		build.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				;
				
			}
		});
		AlertDialog Dialog = build.create();
		Dialog.show();
	}
	
	ProcedureAdapt mProcedureAdapt;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int Code = msg.what;
			if(QUERY_DEVICEID == Code){
				DeviceID = getMyUUID();
				if(null == DeviceID){
					mDialog.setMessage("正在获取设备ID...");
					SendDataMessage(QUERY_DEVICEID, "正在获取设备ID...", 1);
				}
				else{
					mDialog.setMessage("正在检查WIFI网络...");
					/*mDialog.setMessage("正在连接数据库...");
					ThreadData thread = new ThreadData(JobListActivity.this, GET_UPDATEINFO);
					thread.start()*/;
				}
			}
			else if(ERROR_EXIT == Code){
				mDialog.cancel();
				//PlayMusic(MUSIC_ERROR);
				TipError((String)msg.obj, 3, true);
			}
			else if(CONNECT_SUCCESS == Code){
				mDialog.setMessage((String)msg.obj);
				//PlayMusic(MUSIC_RIGHT);
			}
			else if(GETJOBORDER_SUCCESS == Code){
				mDialog.setMessage("查询订单数据成功");
				mDialog.cancel();
				//PlayMusic(MUSIC_RIGHT);
				mListJobOrder = (List<JobOrder>)msg.obj;
				JobOrderAdapt jobadapt = new JobOrderAdapt(JobListActivity.this, mListJobOrder);
				ListJobOrder.setAdapter(jobadapt);
				//-----点检或模糊查询模式进入，直接查询工序
				if(djjr || DatabaseOper.GX_MODE==DatabaseOper.KEYBOARD_MODE){
					mDialog.setMessage("正在查询工序名称...");
					mDialog.show();
					ThreadData thread = new ThreadData(JobListActivity.this, QUERY_PROCEDURE);
					thread.start();
				}
			}
			else if(GETPROCEDURE_SUCCESS == Code){
				mDialog.setMessage("查询工序名称成功");
				mDialog.cancel();
				//PlayMusic(MUSIC_RIGHT);
				//OQC模式只有一个工序，可以自动选择
				if(DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE){
					iSelProcedure=1;
				}
				mListProcedure = (List<Procedure>)msg.obj;
				ProcedureAdapt procedureadapt = new ProcedureAdapt(JobListActivity.this, mListProcedure);
				mProcedureAdapt = procedureadapt;
				ListProcedure.setAdapter(mProcedureAdapt);
				//-----点检或模糊查询模式进入，直接查询材料
				if(djjr || (DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE && mListProcedure.size()==2)){
					mDialog.setMessage("正在查询材料名称...");
					mDialog.show();
					ThreadData thread = new ThreadData(JobListActivity.this, QUERY_MATERIAL);
					thread.start();
				}
				
			}
			else if(GETMATERIAL_SUCCESS == Code){
				mDialog.setMessage("查询材料名称成功");
				mDialog.cancel();
				//PlayMusic(MUSIC_RIGHT);
				mListMaterial = (List<Material>)msg.obj;
				String strResult ="请扫描料号(批次):\n";
				if(1 < mListMaterial.size())
				{
					DatabaseOper.mbScanBatchCode=true;
					for(int i = 1; i < mListMaterial.size(); i++)
					{
						MesLsData.lcldm.add(mListMaterial.get(i).Getmaterial());
						if(mListMaterial.get(i).Getclgkztbz().equals("1") && 
								mListMaterial.get(i).Getmaterialofcontrol().equals("")){
							strResult+=mListMaterial.get(i).Getmaterial();
						}
						if(mListMaterial.get(i).Getpcgkztbz().equals("1") && 
								mListMaterial.get(i).Getbatchkeymaterial().equals("")){
							strResult+="(批次)";
						}
						strResult+="\n";
					}
				}
				else  DatabaseOper.mbScanBatchCode=false;
					
				MaterialAdapt materialadapt = new MaterialAdapt(JobListActivity.this, mListMaterial);
				ListMaterial.setAdapter(materialadapt);
				//-------非OQC, 扫描料号结果
				if(DatabaseOper.SC_MODE!=DatabaseOper.OQC_MODE){
					if(strResult.equals("请扫描料号:\n")) strResult="可点击确定进入SOP";
					strResult += "\n\n右侧黄色显示为务必检验料号及批次，灰色为可扫可不扫料号及不必扫描批次";
					ListJobOrder.setVisibility(View.GONE);
					//ListProcedure.setVisibility(View.GONE);
					RelativeBarcodeResult.setVisibility(View.VISIBLE);
					TextBarcodeResult.setText(strResult);
				}
				
				//-------
				//-----------------------------------------扫描料号完成才能进入SOP
				BtnSave.setEnabled(true);
				BtnYuLan.setEnabled(true);
				/*if(DatabaseOper.mBarcodeNums < mListMaterial.size()-1) {
					DatabaseOper.lBarcodes.add(StringInput);
					DatabaseOper.mBarcodeNums++;
				}*/
				/*if(DatabaseOper.mBarcodeNums == mListMaterial.size()-1) BtnSave.setEnabled(true);
				else {DatabaseOper.mBarcodeNums++; BtnSave.setEnabled(false);}*/
				//-----------------------------------------
				EditInput.setEnabled(true);
				BtnKeyBoard.setEnabled(false);
				/*ListView listview = (ListView)ListMaterial;
				View view = listview.getChildAt(1);
				EditText edittext = (EditText) view.findViewById(R.id.editItem_batchcode);
				edittext.setFocusable(true);
				edittext.setFocusableInTouchMode(true);
				edittext.requestFocus();
				view.setBackgroundColor(SelColor);*/
			}
			else if(SCANBATCHCODE_SUCCESS == Code){
				mDialog.setMessage((String)msg.obj);
				//mDialog.cancel();
				mDialog.setMessage("正在查询材料名称...");
				//mDialog.show();
				//ListMaterial.setAdapter(null);//-------改动点
				/*绿色字体选中*/
				if(null != StringInput){
					//String strInfo = DatabaseOper.SpGetWldm(StringInput);
					
					for(int i = 0; i < mListMaterial.size(); i++)
					{
						String strMaterial = mListMaterial.get(i).Getmaterial();
						if(strMaterial.equals(StringInput) || (null != StringMaterial && strMaterial.equals(StringMaterial))){
							iSelMaterial = i;
							break;
						}
					}
				}
				//-----------------------------------------扫描料号完成才能进入SOP
				/*if(DatabaseOper.mBarcodeNums < mListMaterial.size()-1) {
					DatabaseOper.lBarcodes.add(StringInput);
					DatabaseOper.mBarcodeNums++;
				}*/
				DatabaseOper.mBarcodeNums=0;
				DatabaseOper.mNeedScanBarcodeNums=0;
				DatabaseOper.mBatchNums = 0;
				DatabaseOper.mNeedScanBatchNums=0;
				//-----------------------------------------
				ThreadData thread = new ThreadData(JobListActivity.this, QUERY_MATERIAL);
				thread.start();
				//ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
			}
			else if(GETSOPINFO_SUCCESS == Code){
				mDialog.setMessage("查询SOP信息成功");
				mDialog.cancel();
				//PlayMusic(MUSIC_RIGHT);
				//Intent intent = new Intent(JobListActivity.this, MainActivity.class);
				//OQC模式数据保存
				if(DatabaseOper.SC_MODE == DatabaseOper.OQC_MODE){
					DatabaseOper.OQC_AQL=mListJobOrder.get(iSelJob).Getmoid();
					DatabaseOper.OQC_SJS=mListJobOrder.get(iSelJob).Getmodel();
					DatabaseOper.OQC_CJS=mListJobOrder.get(iSelJob).Getbatch();
				}
				//--------
				Intent intent = new Intent(JobListActivity.this, UserActivity.class);
				
				//intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				String gd = mListJobOrder.get(iSelJob).Getjoborder();
				String gydm=mListJobOrder.get(iSelJob).Getmaterialorder();
				String moeid=mListJobOrder.get(iSelJob).Getmoid();
				String jxbm=mListJobOrder.get(iSelJob).Getmodel();
				String batch=mListJobOrder.get(iSelJob).Getbatch();
				String strprocedurexh = ""+mListProcedure.get(iSelProcedure).Getnumber();
				String strproceduregxmc = mListProcedure.get(iSelProcedure).Getprocedurename();
				intent.putExtra("sopfile", (String)msg.obj);
				intent.putExtra("gd", gd);
				intent.putExtra("gydm", gydm);
				intent.putExtra("jxbm", jxbm);
				intent.putExtra("batch", batch);
				intent.putExtra("moeid", moeid);
				//intent.putExtra("sopfile", "mes.htm");
				intent.putExtra("deviceid", DeviceID);
				intent.putExtra("procedurexh", strprocedurexh);
				intent.putExtra("proceduregxmc", strproceduregxmc);
				//intent.putExtra("ip", IP);
				startActivity(intent);
				//Toast.makeText(getApplicationContext(), (String)msg.obj, 3000).show();
			}
			else if(ERROR_NOEXIT == Code){
				mDialog.cancel();
				//PlayMusic(MUSIC_ERROR);
				TipError((String)msg.obj, 3, false);
			}
			else if(ERRORDIALOG_CANCEL == Code){
				mErrorDialog.cancel();
			}
			else if(UPDATE == Code){
				mDialog.setMessage("检查版本更新成功");
				if(STRING_UPDATE.equals((String)msg.obj)) {
					showUpdateDialog();
				}
				mDialog.setMessage("正在查询订单数据...");
				ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
				thread.start();
				
			}
			else if(UPDATE_ERROR == Code){
				TipError((String)msg.obj, 3, false);
			}
			else if(PROCEDURE_COLORCHANGE == Code){
				MaterialAdapt adapt = (MaterialAdapt) ListMaterial.getAdapter();
				adapt.notifyDataSetChanged();
			}
		};
	};
	
	private void SendDataMessage(int Code, Object Data, int delay){
		Message msg = handler.obtainMessage();
		msg.what = Code;
		msg.obj = Data;
		handler.sendMessageDelayed(msg, delay*1000);
	}
	
	private class ThreadData extends Thread{
		private Context mContex;
		private int query_mode;
		ThreadData(Context contex, int mode){
			this.mContex = contex;
			this.query_mode = mode;
		}
		@Override
		public void run() {
			super.run();
			if(null == DatabaseOper.con)
			{
				/*String UserName = "sa", PassWord = "sql2008", 
						Address = "192.168.1.114:1433", DatabaseName = "mes_yf";
				try {
					UserName = ConfigurationFile.getProfileString(ConfigPath, "Config",
							"username", "sa");
					PassWord = ConfigurationFile.getProfileString(ConfigPath, "Config",
							"password", "sql2008");
					Address = ConfigurationFile.getProfileString(ConfigPath, "Config",
							"address", "192.168.1.114:1433");
					DatabaseName = ConfigurationFile.getProfileString(ConfigPath, "Config",
							"databasename", "mes_yf");
				} catch (IOException e) {
					e.printStackTrace();
					Log.i(TAG, "数据库配置文件不存在或解析错误");
				}
				int itrim = Address.indexOf(":", 0);
				IP = Address.substring(0, itrim);*/
				//DatabaseOper.InitDatabaseConfig(ConfigPath);
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(ERROR_NOEXIT, "数据库连接失败", 0);
					return;
				}else SendDataMessage(CONNECT_SUCCESS, "数据库连接成功", 0);
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
				SendDataMessage(UPDATE, "检查版本更新成功", 0);
			}
			else if(QUERY_JOBORDER == query_mode)
			{
				//SendDataMessage(CONNECT_SUCCESS, "正在查询订单数据...", 0);
				//PlayMusic(MUSIC_SCAN);
				List<JobOrder> ListJoborder;
				if(DatabaseOper.SC_MODE == DatabaseOper.OQC_MODE)  ListJoborder = DatabaseOper.GetJobOrderOQC(DeviceID);
				else  ListJoborder = DatabaseOper.GetJobOrder(DeviceID);
				if( null == ListJoborder){
					SendDataMessage(ERROR_NOEXIT, "查询订单数据失败", 0);
				}else{
					SendDataMessage(GETJOBORDER_SUCCESS, ListJoborder, 0);
				}
			}
			else if(QUERY_PROCEDURE == query_mode)
			{
				//SendDataMessage(CONNECT_SUCCESS, "正在查询工序名称...");
				//int index = ListJobOrder.getSelectedItemPosition();
				//PlayMusic(MUSIC_SCAN);
				String wldm = mListJobOrder.get(iSelJob).Getmaterialorder();
				String moeid= mListJobOrder.get(iSelJob).Getmoid();
				List<Procedure> Listprocedure = DatabaseOper.GetProcedure(wldm, DeviceID,moeid);
				if( null == Listprocedure){
					SendDataMessage(ERROR_NOEXIT, "查询工序名称失败", 0);
				}else{
					SendDataMessage(GETPROCEDURE_SUCCESS, Listprocedure, 0);
				}
			}
			else if(QUERY_MATERIAL == query_mode)
			{
				//SendDataMessage(CONNECT_SUCCESS, "正在查询材料名称...");
				//int index = ListJobOrder.getSelectedItemPosition();
				//PlayMusic(MUSIC_SCAN);
				String moid = mListJobOrder.get(iSelJob).Getmoid();
				String wldm = mListJobOrder.get(iSelJob).Getmaterialorder();
				//index = ListProcedure.getSelectedItemPosition();
				String xh = "" + mListProcedure.get(iSelProcedure).Getnumber();
				List<Material> Listmaterial = DatabaseOper.GetMaterial(moid, wldm, xh, DeviceID);
				if( null == Listmaterial){
					SendDataMessage(ERROR_NOEXIT, "查询材料名称失败", 0);
				}else{
					SendDataMessage(GETMATERIAL_SUCCESS, Listmaterial, 0);
				}
			}
			else if(SCAN_BATCHCODE == query_mode)
			{
				String moid = mListJobOrder.get(iSelJob).Getmoid();
				String PadName = DeviceID;
				String wldm = mListJobOrder.get(iSelJob).Getmaterialorder();
				String xh = "" + mListProcedure.get(iSelProcedure).Getnumber();
				//String batchcode = mListMaterial.get(iSelMaterial).Getbatchcode();
				String batchcode  = StringInput;
				if( ! DatabaseOper.ScanBatchCode(moid, PadName, wldm, xh, batchcode) ){
					mbSelMateria = false;
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, DatabaseOper.ScanResult, 0);
					SendDataMessage(PROCEDURE_COLORCHANGE, "批次码扫描失败, 颜色变化", 0);
				}else{
					mbSelMateria = true;
					StringMaterial = DatabaseOper.SpGetWldm(StringInput);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
					SendDataMessage(SCANBATCHCODE_SUCCESS, DatabaseOper.ScanResult, 0);
				}
				
			}
			else if(QUERY_SOPINFO == query_mode)
			{
				//SendDataMessage(CONNECT_SUCCESS, "正在查询材料名称...");
				//int index = ListJobOrder.getSelectedItemPosition();
				String wldm = mListJobOrder.get(iSelJob).Getmaterialorder();
				//index = ListProcedure.getSelectedItemPosition();
				String xh = "" + mListProcedure.get(iSelProcedure).Getnumber();
				String SopFile = DatabaseOper.GetSopInfo(wldm, xh, DeviceID);
				if( null == SopFile){
					SendDataMessage(ERROR_NOEXIT, "查询SOP失败", 0);
				}else{
					SendDataMessage(GETSOPINFO_SUCCESS, SopFile, 0);
				}
			}
			
		}
		
	}
	
	private static class JobOrderHolder {
        TextView number;
        TextView producedata;
        TextView joborder;
        TextView materialorder;
        TextView model;
        TextView batch;
	}
	
	private class JobOrderAdapt extends BaseAdapter{
		Context context;
		List<JobOrder> list;
		
		JobOrderAdapt(Context context, List<JobOrder> List){
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
			View view = inflater.inflate(R.layout.joborderitem, null);
			if(0 != iSelJob&& position ==iSelJob )
			    view.setBackgroundColor(SelColor);
			JobOrderHolder holder = new JobOrderHolder();
			holder.number = (TextView) view.findViewById(R.id.textItem_number);
			holder.producedata = (TextView) view.findViewById(R.id.textItem_producedata);
			holder.joborder = (TextView) view.findViewById(R.id.textItem_joborder);
			holder.materialorder = (TextView) view.findViewById(R.id.textItem_materialorder);
			holder.model = (TextView) view.findViewById(R.id.textItem_model);
			holder.batch = (TextView) view.findViewById(R.id.textItem_batch);
			/*int	TextColor = CancelSelTextColor;
			int	BackgroundColor = Color.WHITE;
			holder.number.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.edit_text));
			holder.producedata.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.edit_text));
			holder.joborder.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.edit_text));
			holder.materialorder.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.edit_text));
			holder.model.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.edit_text));
			holder.batch.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.edit_text));
		    holder.materialorder.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.edit_text));
			
			holder.number.setBackgroundColor(BackgroundColor);
			holder.producedata.setBackgroundColor(BackgroundColor);
			holder.joborder.setBackgroundColor(BackgroundColor);
			holder.materialorder.setBackgroundColor(BackgroundColor);
			holder.model.setBackgroundColor(BackgroundColor);
			holder.batch.setBackgroundColor(BackgroundColor);
			
			holder.number.setTextColor(TextColor);
			holder.producedata.setTextColor(TextColor);
			holder.joborder.setTextColor(TextColor);
			holder.materialorder.setTextColor(TextColor);
			holder.model.setTextColor(TextColor);
			holder.batch.setTextColor(TextColor);*/
			
			int number = list.get(position).Getnumber();
			if(0 == number) {
				view.setClickable(false);
				holder.number .setText("序 号");
			}
			else {
				holder.number .setText(""+number);
				holder.number.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						/*ProcedureAdapt procedureadapt = new ProcedureAdapt(JobListActivity.this, mListProcedure);
						ListProcedure.setAdapter(procedureadapt);
						ListMaterial.setAdapter(null);
						Log.i(TAG, "procedureadapt");*/
					}
				});
			}
			holder.producedata .setText(list.get(position).Getproducedata());
			holder.joborder .setText(list.get(position).Getjoborder());
			holder.materialorder .setText(list.get(position).Getmaterialorder());
			holder.model.setText(list.get(position).Getmodel());
			holder.batch.setText(list.get(position).Getbatch());
			//检测是否为点检或模糊查询模式进入
			if(djjr || DatabaseOper.GX_MODE==DatabaseOper.KEYBOARD_MODE ){
				if(position == gdxh || 0 == number)
					return view;
					else {
						View view1 = new View(context);
						return view1;
					}
			}
			//直接显示多工单模式
			if(DatabaseOper.GX_MODE==DatabaseOper.KEYBOARD_DISPLAYGD){
				if(ListKeyBoard.contains(position) || 0 == number)
					return view;
					else {
						View view1 = new View(context);
						return view1;
					}
			}
			/*else if(DatabaseOper.GX_MODE==DatabaseOper.OQC_MODE){
				return view;
			}*/
			else return view;
		}
		
	}
	
	private static class ProcedureHolder {
        TextView number;
        TextView procedurename;
        //TextView name;
	}
	
	private class ProcedureAdapt extends BaseAdapter{
		Context context;
		List<Procedure> list;
		
		ProcedureAdapt(Context context, List<Procedure> List){
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
			View view = inflater.inflate(R.layout.procedureitem, null);
			if(0 != iSelProcedure && position ==iSelProcedure )
			    view.setBackgroundColor(SelColor);
			//检测是否为模糊查询模式
			/*if(0 != iSelProcedure &&DatabaseOper.GX_MODE==DatabaseOper.KEYBOARD_MODE){
				view.setBackgroundColor(SelColor);
			}*/
			ProcedureHolder holder = new ProcedureHolder();
			holder.number = (TextView) view.findViewById(R.id.textItem_number);
			holder.procedurename = (TextView) view.findViewById(R.id.textItem_procedurename);
			//holder.name = (TextView) view.findViewById(R.id.textItem_name);
			int number = list.get(position).Getnumber();
			if(0 == number) {
				view.setClickable(false);
				holder.number .setText("序 号");
			}
			else {
				holder.number .setText(""+number);
				holder.number.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						/*MaterialAdapt materiaadapt = new MaterialAdapt(JobListActivity.this, mListMaterial);
						ListMaterial.setAdapter(materiaadapt);
						Log.i(TAG, "materiaadapt");*/
					}
				});
			}
			
			holder.procedurename .setText(list.get(position).Getprocedurename());
			//holder.name.setText(list.get(position).Getname());
			//检测是否为点检进入
			if(djjr){
				if(position == gxxh || 0 == number)
					return view;
					else {
						View view1 = new View(context);
						return view1;
					}
			}
			
			else return view;
		}
		
	}
	
	private static class MaterialHolder {
        TextView number;
        TextView material;
        TextView pmgg;
        EditText batchcode;
	}
	
	private class  MaterialAdapt extends BaseAdapter{
		Context context;
		List<Material> list;
		boolean bEnter = false;
		String mstr = "";
		
		MaterialAdapt(Context context, List<Material> List){
			this.context = context;
			this.list = List;
			bEnter = false;
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
			final int index = position;
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.materialitem, null);
			//-------------------
			/*if(0 != iSelMaterial&& position ==iSelMaterial ){
				if(mbSelMateria)  view.setBackgroundColor(SelColor);
				else  view.setBackgroundColor(SelWrongColor);
			}*/
			
			final MaterialHolder holder = new MaterialHolder();
			holder.number = (TextView) view.findViewById(R.id.textItem_number);
			holder.material = (TextView) view.findViewById(R.id.textItem_material);
			holder.pmgg = (TextView) view.findViewById(R.id.textItem_pmgg);
			holder.batchcode = (EditText) view.findViewById(R.id.editItem_batchcode);
			int number = list.get(position).Getnumber();
			holder.batchcode.clearFocus();
			//holder.batchcode.setInputType(InputType.TYPE_NULL); 
			//holder.batchcode.setKeyListener(null);
			if(0 == number) {
				holder.number .setText("序 号");
				//holder.batchcode.setFocusable(false);
				//holder.batchcode.setEnabled(false);
			}
			else {
				holder.number.setText(""+number);
				//holder.number.clearFocus();
				//holder.number.setClickable(false);
				//holder.batchcode.setFocusable(false);
				//holder.batchcode.setEnabled(false);
			   
				holder.batchcode.setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						 if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {  
							 Toast.makeText(JobListActivity.this, "keyCode: " + keyCode, 2000).show();
							 iSelMaterial = index;
							 String batchcode = holder.batchcode.getText().toString();
							 mDialog.setMessage("正在扫描批次码...");
							 mDialog.show();
							 list.get(index).Setbatchkeymaterial(batchcode);
							 mListMaterial.get(index).Setbatchkeymaterial(batchcode);
							 ThreadData thread = new ThreadData(JobListActivity.this, SCAN_BATCHCODE);
							 thread.start();
							 list.get(index).bEnter = true;
							 return true;
			                }  
			                return false;
					}
				});
				holder.batchcode.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence arg0, int start, int before, int count) {
						String str = arg0.toString();
						int Len = str.length();
						//holder.batchcode.gets
						//若holder.batchcode之前有数据则count不为1
						if(1 == count && list.get(index).bEnter) {
							list.get(index).bEnter = false;
							//holder.batchcode.setText(str.substring(start+1, start+2));
							//holder.batchcode.setSelection(1);
							holder.batchcode.setText(str.substring(start, start+1));
							holder.batchcode.setSelection(1);
						}
						if(str.endsWith("\n")){
							Toast.makeText(JobListActivity.this, "start: "+start+"before: "+before+"count: "+count, 4000).show();
						}
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
						
					}
				});
				holder.batchcode.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View arg0, boolean focus) {
						if(focus) {
							if(! holder.batchcode.isFocusableInTouchMode()) holder.batchcode.setFocusableInTouchMode(true);
							int Len = holder.batchcode.getEditableText().toString().length();
							holder.batchcode.requestFocus();
							holder.batchcode.setSelection(Len);
							//holder.batchcode.setText("");
							//holder.batchcode.setSelection(0);
						}
					}
				});
				holder.batchcode.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if(! holder.batchcode.isFocusableInTouchMode()) holder.batchcode.setFocusableInTouchMode(true);
						//int Len = holder.batchcode.getEditableText().toString().length();
						//holder.batchcode.setSelection(Len);
						//holder.batchcode.setText("");
						//holder.batchcode.setSelection(0);
					}
				});
				/*if(1 != number)  {
					holder.batchcode.setFocusableInTouchMode(false);	
					holder.batchcode.clearFocus();
				}*/
				//else holder.batchcode.requestFocus();
				//holder.batchcode.setText("J13120900"+(1+position));
			}
			
			holder.material.setText(list.get(position).Getmaterial());
			holder.pmgg.setText(list.get(position).Getpmgg());
			
			
			String clgkztbz = list.get(position).Getclgkztbz();
			if(clgkztbz.equals("1")){
				String materialofcontrol = list.get(position).Getmaterialofcontrol();
				//不为空说明已经扫描
				if(materialofcontrol.equals("")) holder.material.setBackgroundColor(SelTextColor);
				else holder.material.setBackgroundColor(SelColor);
				
			}else if(clgkztbz.equals("0") && 0 != number){
				holder.material.setBackgroundColor(NONeedSelBackgroundColor);
			}
			
			String batchcode = list.get(position).Getbatchkeymaterial();
			holder.batchcode.setText(batchcode);
			//holder.batchcode.setBackgroundColor(Color.TRANSPARENT);
			holder.batchcode.setSelection(batchcode.length());
			if(null == batchcode) list.get(position).bEnter = false;
			else {
				//-------区分是否需要管控
				//String strztbz = list.get(position).Getclgkztbz();
				//if(strztbz.equals("1")) view.setBackgroundColor(SelTextColor);
				//else if(strztbz.equals("0") && 0 != number) view.setBackgroundColor(NONeedSelBackgroundColor);
				//-------
				//---------------------------------扫描料号完成才能进入SOP
				//if(-1 != DatabaseOper.lBarcodes.indexOf(batchcode)) view.setBackgroundColor(SelColor);
				//if(! batchcode.equals("") && 0 != number) view.setBackgroundColor(SelColor);
				//---------------------------------
				
				//料是否有批次号
				String strpcgk = list.get(position).Getpcgkztbz();
				if(strpcgk.equals("1")) {
					//不为空说明已经扫描
					if(batchcode.equals("")) holder.batchcode.setBackgroundColor(SelTextColor);
					else holder.batchcode.setBackgroundColor(SelColor);
				}
				else if(strpcgk.equals("0") && 0 != number) holder.batchcode.setBackgroundColor(NONeedSelBackgroundColor);
				
				list.get(position).bEnter = true;
			}
			return view;
		}
		
	}
}
