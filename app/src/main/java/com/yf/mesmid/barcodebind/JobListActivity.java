package com.yf.mesmid.barcodebind;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yf.mesmid.db.DatabaseOper;
import com.yf.mesmid.entity.Material;
import com.yf.mesmid.entity.Procedure;
import com.yf.mesmid.R;
import com.yf.mesmid.app.KeyBoard;
import com.yf.mesmid.app.NotificationUpdateActivity;
import com.yf.mesmid.util.ScanSound;
import com.yf.mesmid.service.WifiService;
import com.yf.mesmid.entity.JobOrder;
import com.yf.mesmid.app.MyApp;
import com.yf.mesmid.entity.UpdataInfo;

import android.annotation.SuppressLint;
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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
	
	private final String STRING_UPDATE = "APP�и���";
	private final String STRING_NOUPDATE = "APP�޸���";
	
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
	//----���APK����
	private boolean djjr = false;
	private int gdxh = 0;
	private int gxxh = 0;
	private String user = "";
	//----�Ϻ�ɨ����
	private RelativeLayout RelativeBarcodeResult;
	private TextView TextBarcodeResult;
	private Button BtnCancle;
	//----С��������
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
		if(DatabaseOper.SC_MODE==DatabaseOper.ZC_MODE) text_itemname.setText("JobOrder ��������");
		else if(DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE) text_itemname.setText("OQC ��������");
		else if(DatabaseOper.SC_MODE==DatabaseOper.FG_MODE) text_itemname.setText("JobOrder ���ڷ���");
		//CheckUpdate();
		//----�Ϻ�ɨ����
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
		//--������
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
				//-----------------------------------------ɨ�����ɨ���Ϻ���ɲ��ܽ���SOP
				if(DatabaseOper.mBarcodeNums == DatabaseOper.mNeedScanBarcodeNums 
						&& DatabaseOper.mBatchNums == DatabaseOper.mNeedScanBatchNums) {
					MesLsData.bYuLang = false;
					mDialog.setMessage("���ڲ�ѯSOP...");
					mDialog.show();
					ThreadData thread = new ThreadData(JobListActivity.this, QUERY_SOPINFO);
					thread.start();
				}
				else {
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					TipError("\n����ɨ�����Ϻż�����\n", 2, false);
				}
				//-----------------------------------------
				/*mDialog.setMessage("���ڲ�ѯSOP...");
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
					mDialog.setMessage("�������²�ѯ...");
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
					mDialog.setMessage("���ڼ��WIFI����...");
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
				/*���ţ�ɨ��Ԥ������(YULANGSOP),��Ԥ��SOP5����*/
				MesLsData.bYuLang = true;
				mDialog.setMessage("���ڲ�ѯSOP...");
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
					//---С����ģ����ѯ
					if(DatabaseOper.GX_MODE==DatabaseOper.KEYBOARD_MODE){
						List<Integer> list =GetJobOrderContainList(mListJobOrder, strBuff);
						int iCount=list.get(0);
						if(iCount<=0) {
							DatabaseOper.GX_MODE=DatabaseOper.ZC_MODE;
							Toast.makeText(getApplicationContext(), "δ�ܲ�ѯ��ƥ�乤��", Toast.LENGTH_LONG).show();
						}
						/*ģ����ѯ��һ���������ֱ�Ӳ鹤��*/
						else if(iCount==1){
							gdxh=list.get(1);
							iSelJob=gdxh;
							if(DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE) iSelProcedure =1;
							//djjr=true;
							DatabaseOper.GX_MODE=DatabaseOper.KEYBOARD_MODE;
							mDialog.setMessage("���ڲ�ѯ��������...");
							ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
							thread.start();
						}
						/*ģ����ѯ���������, ��ֱ����ʾ�๤��*/
						else if(iCount>=2){
							list.remove(0);
							ListKeyBoard=list;
							DatabaseOper.GX_MODE=DatabaseOper.KEYBOARD_DISPLAYGD;
							mDialog.setMessage("���ڲ�ѯ��������...");
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
									mDialog.setMessage("���ڲ�ѯ��������...");
									ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
									thread.start();
								}
							}
						}*/
					}
					/*���ţ���ɨ���Ϻ�ǿ�ƽ���SOP*/
					if(strBuff.equals(MesLsData.QIANGZHI_BARCODE) && BtnSave.isEnabled()){
						MesLsData.bYuLang = false;
						mDialog.setMessage("���ڲ�ѯSOP...");
						mDialog.show();
						ThreadData thread = new ThreadData(JobListActivity.this, QUERY_SOPINFO);
						thread.start();
						return;
					}
					/*���ţ�ɨ��Ԥ������(YULANGSOP),��Ԥ��SOP5����*/
					if(strBuff.equals(MesLsData.YULANGSOP_BARCODE)){
						MesLsData.bYuLang = true;
						mDialog.setMessage("���ڲ�ѯSOP...");
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
						mDialog.setMessage("����ɨ������...");
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
		//JobOrder joborder = new JobOrder(i, "�� �� ��", "���ʹ���", "��������", null);
		/*mListMaterial.add(new Material(0, "��    ��"));
		for(int i = 1; i < 20; i++)
		{
			Material material = new Material(i, "��˿��");
			mListMaterial.add(material);
		}
		mListProcedure.add(new Procedure(0, "��    ��", "��    ��", null));
		for(int i = 1; i < 20; i++)
		{
			Procedure procedure = new Procedure(i, "PC6953MB5003", "ɨ��ƽ��", null);
			mListProcedure.add(procedure);
		}
		mListJobOrder.add(new JobOrder(0, "�� �� ��", "���ʹ���", "��������", null));
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
				//���������Ϊ���ģʽ
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
				mDialog.setMessage("���ڲ�ѯ��������...");
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
				//���������Ϊ���ģʽ
				djjr = false;
				//DatabaseOper.GX_MODE=DatabaseOper.ZC_MODE;
				//------------
				ListView listview = (ListView)arg0;
				//Parcelable state = ListProcedure.onSaveInstanceState();//����ListProcedure����
				ProcedureAdapt adapt = (ProcedureAdapt) listview.getAdapter();
				adapt.notifyDataSetChanged();
				//mProcedureAdapt.notifyDataSetChanged();
				//ListProcedure.setAdapter(adapt);
				
				//ListProcedure.onRestoreInstanceState(state);//�������ݻ�ԭ
				//View view = listview.getChildAt(iSelProcedure);
				//View view = listview.getAdapter().getView(iSelProcedure,null,arg0);
				//view.setBackgroundColor(Color.TRANSPARENT);
				//arg1.setBackgroundColor(SelColor);
				
				/*MaterialAdapt materiaadapt = new MaterialAdapt(JobListActivity.this, mListMaterial);
				ListMaterial.setAdapter(materiaadapt);*/
				mDialog.setMessage("���ڲ�ѯ��������...");
				mDialog.show();
				ListMaterial.setAdapter(null);//ListMaterial.set
				MesLsData.lcldm.clear();//�����������
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
		Wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("���ڻ�ȡ�豸ID...");
		mDialog.show();
		DeviceID = getMyUUID();
		//DeviceID="50524553542400A2";
		//DeviceID="5052455354240010";
		if(null == DeviceID){
			SendDataMessage(QUERY_DEVICEID, "���ڻ�ȡ�豸ID...", 1);
		}
		else{
			mDialog.setMessage("���ڼ��WIFI����...");
			//mDialog.setMessage("�����������ݿ�...");
			//ThreadData thread = new ThreadData(this, GET_UPDATEINFO);
			//thread.start();
		}
		//DatabaseOper.InitDatabaseConfig(ConfigPath);
		//���Ѳ���������
		WifiReceiver = new WIFIBroadcastReceiver();
		IntentFilter Wififilter = new IntentFilter();
		Wififilter.addAction(DatabaseOper.FirstWIFI_MSG);
		Wififilter.addAction(DatabaseOper.RepeatWIFI_MSG);
		registerReceiver(WifiReceiver, Wififilter);
		/*Intent intent = new Intent(this, WifiService.class);
		intent.putExtra("repeatquery", false);
		startService(intent);*/
		//CountThread();
		mDialog.setMessage("���ڲ�ѯ��������...");
		ThreadData thread = new ThreadData(JobListActivity.this, QUERY_JOBORDER);
		thread.start();
		//----С��������
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
					/*if(str.length()>=5) Toast.makeText(getApplicationContext(), "ģ����ѯ���ó���5λ����", 3000).show();
					else EditInput.setText(str+key);*/
				}
				
			}
			
		}
	};
	
	//�õ�����ģ����ѯƥ�����
	int GetJobOrderContainNums(List<JobOrder> list, String strContain){
		int count = 0;
		for(int i = 0; i < mListJobOrder.size();i++)
		{
			String zzdh=mListJobOrder.get(i).getJoborder();
			if(zzdh !=null && zzdh.length()>=1){
				//zzdh=zzdh.substring(zzdh.length()-5, zzdh.length());
				if(zzdh.contains(strContain)){
					count++;
				}
			}
		}
		return count;
	}
	
	//�õ�����ģ����ѯƥ������
	List<Integer> GetJobOrderContainList(List<JobOrder> list, String strContain){
		List<Integer> listcontain = new ArrayList<Integer>();
		int count = 0;
		for(int i = 0; i < mListJobOrder.size();i++)
		{
			String zzdh=mListJobOrder.get(i).getJoborder();
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
				mDialog.setMessage("�����������ݿ�...");
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
				mDialog.setMessage("�������²�ѯ...");
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
			mDialog.setMessage("�����������ݿ�...");
			ThreadData thread = new ThreadData(this, GET_UPDATEINFO);
			thread.start();
		}

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		//2Сʱת��ʱ
		/*Toast.makeText(this, "JobList", 4000).show();
		ListJobOrder.setVisibility(View.GONE);
		ListProcedure.setVisibility(View.GONE);*/
		//��SOPҳ�漴���˳�
		boolean bScanBatchCode = intent.getBooleanExtra("scanbatchcode", false);
		if(bScanBatchCode){
			ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_SCAN);
			TipScanBatchCodeEx("\n�ѵ�2Сʱ����ɨ���Ϻ�\n", false);
			//-----------------------------------------ɨ���Ϻ���ɲ��ܽ���SOP
			DatabaseOper.lBarcodes.clear();
			DatabaseOper.lBatchs.clear();
			DatabaseOper.mBarcodeNums = 0;
			DatabaseOper.mNeedScanBarcodeNums=0;
			DatabaseOper.mBatchNums = 0;
			DatabaseOper.mNeedScanBatchNums=0;
			mDialog.setMessage("���ڲ�ѯ��������...");
			mDialog.show();
			ListMaterial.setAdapter(null);
			MesLsData.lcldm.clear();//�����������
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
	 * @return ��ȡ��ǰӦ�ó���İ汾��
	 */
	private String getVersion() {
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {

			e.printStackTrace();
			return "�汾��δ֪";
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
						SendDataMessage(UPDATE, "APP�и���", 0);
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
	 *            ��ǰ�ͻ��˵İ汾����Ϣ
	 * @return �Ƿ���Ҫ����
	 */
	private boolean isNeedUpdate(String versiontext) {
		//GetUpdataInfo updateinfo = new GetUpdataInfo();//��xml�л�ȡ
		try {
			//info = updateinfo.getUpdataInfo();
			String version = info.getVersion();
			if (versiontext.equals(version)) {
				Log.i(TAG, "�汾��ͬ,��������, ����������");
				//loadMainUI();
				return false;
			} else {
				Log.i(TAG, "�汾��ͬ,��Ҫ����");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			SendDataMessage(UPDATE_ERROR, "��ȡ������Ϣ�쳣", 0);
			Log.i(TAG, "��ȡ������Ϣ�쳣, ����������");
			//loadMainUI();
			return false;
		}

	}
	
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��⵽�°汾 V" + info.getVersion());
		builder.setMessage(info.getDescription() + "\n\n" + "�Ƿ����ظ���?");
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(null == info.getApkurl()){
					SendDataMessage(UPDATE_ERROR, "APK��ַ��ȡʧ��", 0);
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
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

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
	 * �õ�wifi mac��ַ
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
		build.setTitle("�쳣��ʾ");
		build.setMessage(strInfo);
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
		
		SendDataMessage(ERRORDIALOG_CANCEL, "����Ի�����ʧ", time);
	}
	
	private void TipScanBatchCode(String strInfo, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(JobListActivity.this);
		build.setTitle("��ɨ�������Ϻ�");
		build.setMessage(strInfo);
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
		
		SendDataMessage(ERRORDIALOG_CANCEL, "����Ի�����ʧ", 8);
	}
	
	private void TipScanBatchCodeEx(String strInfo, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(JobListActivity.this);
		build.setTitle("��ɨ�������Ϻ�");
		build.setMessage(strInfo);
		build.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
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
					mDialog.setMessage("���ڻ�ȡ�豸ID...");
					SendDataMessage(QUERY_DEVICEID, "���ڻ�ȡ�豸ID...", 1);
				}
				else{
					mDialog.setMessage("���ڼ��WIFI����...");
				}
			}
			else if(ERROR_EXIT == Code){
				mDialog.cancel();
				TipError((String)msg.obj, 3, true);
			}
			else if(CONNECT_SUCCESS == Code){
				mDialog.setMessage((String)msg.obj);
			}
			else if(GETJOBORDER_SUCCESS == Code){
				mDialog.setMessage("��ѯ�������ݳɹ�");
				mDialog.cancel();
				mListJobOrder = (List<JobOrder>)msg.obj;
				JobOrderAdapt jobadapt = new JobOrderAdapt(JobListActivity.this, mListJobOrder);
				ListJobOrder.setAdapter(jobadapt);
				if(djjr || DatabaseOper.GX_MODE==DatabaseOper.KEYBOARD_MODE){
					mDialog.setMessage("���ڲ�ѯ��������...");
					mDialog.show();
					ThreadData thread = new ThreadData(JobListActivity.this, QUERY_PROCEDURE);
					thread.start();
				}
			}
			else if(GETPROCEDURE_SUCCESS == Code){
				mDialog.setMessage("��ѯ�������Ƴɹ�");
				mDialog.cancel();
				if(DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE){
					iSelProcedure=1;
				}
				mListProcedure = (List<Procedure>)msg.obj;
				ProcedureAdapt procedureadapt = new ProcedureAdapt(JobListActivity.this, mListProcedure);
				mProcedureAdapt = procedureadapt;
				ListProcedure.setAdapter(mProcedureAdapt);
				if(djjr || (DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE && mListProcedure.size()==2)){
					mDialog.setMessage("���ڲ�ѯ��������...");
					mDialog.show();
					ThreadData thread = new ThreadData(JobListActivity.this, QUERY_MATERIAL);
					thread.start();
				}
				
			}
			else if(GETMATERIAL_SUCCESS == Code){
				mDialog.setMessage("��ѯ�������Ƴɹ�");
				mDialog.cancel();
				//PlayMusic(MUSIC_RIGHT);
				mListMaterial = (List<Material>)msg.obj;
				String strResult ="��ɨ���Ϻ�(����):\n";
				if(1 < mListMaterial.size())
				{
					DatabaseOper.mbScanBatchCode=true;
					for(int i = 1; i < mListMaterial.size(); i++)
					{
						MesLsData.lcldm.add(mListMaterial.get(i).getMaterial());
						if(mListMaterial.get(i).getClgkztbz().equals("1") &&
								mListMaterial.get(i).getMaterialofcontrol().equals("")){
							strResult+=mListMaterial.get(i).getMaterial();
						}
						if(mListMaterial.get(i).getPcgkztbz().equals("1") &&
								mListMaterial.get(i).getBatchkeymaterial().equals("")){
							strResult+="(����)";
						}
						strResult+="\n";
					}
				}
				else  DatabaseOper.mbScanBatchCode=false;
					
				MaterialAdapt materialadapt = new MaterialAdapt(JobListActivity.this, mListMaterial);
				ListMaterial.setAdapter(materialadapt);
				//-------��OQC, ɨ���ϺŽ��
				if(DatabaseOper.SC_MODE!=DatabaseOper.OQC_MODE){
					if(strResult.equals("��ɨ���Ϻ�:\n")) strResult="�ɵ��ȷ������SOP";
					strResult += "\n\n�Ҳ��ɫ��ʾΪ��ؼ����Ϻż����Σ���ɫΪ��ɨ�ɲ�ɨ�Ϻż�����ɨ������";
					ListJobOrder.setVisibility(View.GONE);
					//ListProcedure.setVisibility(View.GONE);
					RelativeBarcodeResult.setVisibility(View.VISIBLE);
					TextBarcodeResult.setText(strResult);
				}
				
				//-------
				//-----------------------------------------ɨ���Ϻ���ɲ��ܽ���SOP
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
				mDialog.setMessage("���ڲ�ѯ��������...");
				//mDialog.show();
				//ListMaterial.setAdapter(null);//-------�Ķ���
				/*��ɫ����ѡ��*/
				if(null != StringInput){
					//String strInfo = DatabaseOper.SpGetWldm(StringInput);
					
					for(int i = 0; i < mListMaterial.size(); i++)
					{
						String strMaterial = mListMaterial.get(i).getMaterial();
						if(strMaterial.equals(StringInput) || (null != StringMaterial && strMaterial.equals(StringMaterial))){
							iSelMaterial = i;
							break;
						}
					}
				}
				//-----------------------------------------ɨ���Ϻ���ɲ��ܽ���SOP
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
				mDialog.setMessage("��ѯSOP��Ϣ�ɹ�");
				mDialog.cancel();
				//PlayMusic(MUSIC_RIGHT);
				//Intent intent = new Intent(JobListActivity.this, MainActivity.class);
				//OQCģʽ���ݱ���
				if(DatabaseOper.SC_MODE == DatabaseOper.OQC_MODE){
					DatabaseOper.OQC_AQL=mListJobOrder.get(iSelJob).getModel();
					DatabaseOper.OQC_SJS=mListJobOrder.get(iSelJob).getModel();
					DatabaseOper.OQC_CJS=mListJobOrder.get(iSelJob).getModel();
				}
				//--------
				Intent intent = new Intent(JobListActivity.this, UserActivity.class);
				
				//intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				String gd = mListJobOrder.get(iSelJob).getJoborder();
				String gydm=mListJobOrder.get(iSelJob).getMaterialorder();
				String moeid=mListJobOrder.get(iSelJob).getMoid();
				String jxbm=mListJobOrder.get(iSelJob).getModel();
				String batch=mListJobOrder.get(iSelJob).getBatch();
				String strprocedurexh = ""+mListProcedure.get(iSelProcedure).getNumber();
				String strproceduregxmc = mListProcedure.get(iSelProcedure).getProcedureName();
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
				mDialog.setMessage("���汾���³ɹ�");
				if(STRING_UPDATE.equals((String)msg.obj)) {
					showUpdateDialog();
				}
				mDialog.setMessage("���ڲ�ѯ��������...");
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
					Log.i(TAG, "���ݿ������ļ������ڻ��������");
				}
				int itrim = Address.indexOf(":", 0);
				IP = Address.substring(0, itrim);*/
				//DatabaseOper.InitDatabaseConfig(ConfigPath);
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(ERROR_NOEXIT, "���ݿ�����ʧ��", 0);
					return;
				}else SendDataMessage(CONNECT_SUCCESS, "���ݿ����ӳɹ�", 0);
			}
			if(GET_UPDATEINFO == query_mode)
			{
				SendDataMessage(CONNECT_SUCCESS, "���ڼ��汾����...", 0);
				UpdataInfo updatainfo = DatabaseOper.GetUpdateInfo();
				if(null == updatainfo){
					SendDataMessage(ERROR_NOEXIT, "���汾����ʧ��", 0);
				}else{
					info = updatainfo;
					String strUpdate = null;
					if(isNeedUpdate(curversion)){
						strUpdate = STRING_UPDATE;
					}else strUpdate = STRING_NOUPDATE;
					SendDataMessage(UPDATE, strUpdate, 0);
				}
				SendDataMessage(UPDATE, "���汾���³ɹ�", 0);
			}
			else if(QUERY_JOBORDER == query_mode)
			{
				//SendDataMessage(CONNECT_SUCCESS, "���ڲ�ѯ��������...", 0);
				//PlayMusic(MUSIC_SCAN);
				List<JobOrder> ListJoborder;
//				if(DatabaseOper.SC_MODE == DatabaseOper.OQC_MODE)  ListJoborder = DatabaseOper.GetJobOrderOQC(DeviceID);
//				else  ListJoborder = DatabaseOper.GetJobOrder(DeviceID);
//				if( null == ListJoborder){
//					SendDataMessage(ERROR_NOEXIT, "��ѯ��������ʧ��", 0);
//				}else{
//					SendDataMessage(GETJOBORDER_SUCCESS, ListJoborder, 0);
//				}
			}
			else if(QUERY_PROCEDURE == query_mode)
			{
				//SendDataMessage(CONNECT_SUCCESS, "���ڲ�ѯ��������...");
				//int index = ListJobOrder.getSelectedItemPosition();
				//PlayMusic(MUSIC_SCAN);
				//String wldm = mListJobOrder.get(iSelJob).Getmaterialorder();
				String moeid= mListJobOrder.get(iSelJob).getMoid();
				//List<Procedure> Listprocedure = DatabaseOper.GetProcedure(wldm, DeviceID,moeid);
//				if( null == Listprocedure){
//					SendDataMessage(ERROR_NOEXIT, "��ѯ��������ʧ��", 0);
//				}else{
//					SendDataMessage(GETPROCEDURE_SUCCESS, Listprocedure, 0);
//				}
			}
			else if(QUERY_MATERIAL == query_mode)
			{
				//SendDataMessage(CONNECT_SUCCESS, "���ڲ�ѯ��������...");
				//int index = ListJobOrder.getSelectedItemPosition();
				//PlayMusic(MUSIC_SCAN);
				String moid = mListJobOrder.get(iSelJob).getMoid();
				String wldm = mListJobOrder.get(iSelJob).getMaterialorder();
				//index = ListProcedure.getSelectedItemPosition();
				String xh = "" + mListProcedure.get(iSelProcedure).getNumber();
				//List<Material> Listmaterial = DatabaseOper.getMaterial(moid, wldm, xh, DeviceID);
//				if( null == Listmaterial){
//					SendDataMessage(ERROR_NOEXIT, "��ѯ��������ʧ��", 0);
//				}else{
//					SendDataMessage(GETMATERIAL_SUCCESS, Listmaterial, 0);
//				}
			}
			else if(SCAN_BATCHCODE == query_mode)
			{
				String moid = mListJobOrder.get(iSelJob).getMoid();
				String PadName = DeviceID;
				String wldm = mListJobOrder.get(iSelJob).getMaterialorder();
				String xh = "" + mListProcedure.get(iSelProcedure).getNumber();
				//String batchcode = mListMaterial.get(iSelMaterial).Getbatchcode();
				String batchcode  = StringInput;
				if( ! DatabaseOper.ScanBatchCode(moid, PadName, wldm, xh, batchcode) ){
					mbSelMateria = false;
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, DatabaseOper.ScanResult, 0);
					SendDataMessage(PROCEDURE_COLORCHANGE, "������ɨ��ʧ��, ��ɫ�仯", 0);
				}else{
					mbSelMateria = true;
					StringMaterial = DatabaseOper.SpGetWldm(StringInput);
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
					SendDataMessage(SCANBATCHCODE_SUCCESS, DatabaseOper.ScanResult, 0);
				}
				
			}
			else if(QUERY_SOPINFO == query_mode)
			{
				//SendDataMessage(CONNECT_SUCCESS, "���ڲ�ѯ��������...");
				//int index = ListJobOrder.getSelectedItemPosition();
				String wldm = mListJobOrder.get(iSelJob).getMaterialorder();
				//index = ListProcedure.getSelectedItemPosition();
				String xh = "" + mListProcedure.get(iSelProcedure).getNumber();
				String SopFile = DatabaseOper.GetSopInfo(wldm, xh, DeviceID);
				if( null == SopFile){
					SendDataMessage(ERROR_NOEXIT, "��ѯSOPʧ��", 0);
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
			
			int number = list.get(position).getNumber();
			if(0 == number) {
				view.setClickable(false);
				holder.number .setText("�� ��");
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
			holder.producedata .setText(list.get(position).getProducedata());
			holder.joborder .setText(list.get(position).getJoborder());
			holder.materialorder .setText(list.get(position).getMaterialorder());
			holder.model.setText(list.get(position).getModel());
			holder.batch.setText(list.get(position).getBatch());
			//����Ƿ�Ϊ����ģ����ѯģʽ����
			if(djjr || DatabaseOper.GX_MODE==DatabaseOper.KEYBOARD_MODE ){
				if(position == gdxh || 0 == number)
					return view;
					else {
						View view1 = new View(context);
						return view1;
					}
			}
			//ֱ����ʾ�๤��ģʽ
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
			//����Ƿ�Ϊģ����ѯģʽ
			/*if(0 != iSelProcedure &&DatabaseOper.GX_MODE==DatabaseOper.KEYBOARD_MODE){
				view.setBackgroundColor(SelColor);
			}*/
			ProcedureHolder holder = new ProcedureHolder();
			holder.number = (TextView) view.findViewById(R.id.textItem_number);
			holder.procedurename = (TextView) view.findViewById(R.id.textItem_procedurename);
			//holder.name = (TextView) view.findViewById(R.id.textItem_name);
			int number = list.get(position).getNumber();
			if(0 == number) {
				view.setClickable(false);
				holder.number .setText("�� ��");
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
			
			holder.procedurename .setText(list.get(position).getProcedureName());
			//holder.name.setText(list.get(position).Getname());
			//����Ƿ�Ϊ������
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
			int number = list.get(position).getNumber();
			holder.batchcode.clearFocus();
			//holder.batchcode.setInputType(InputType.TYPE_NULL); 
			//holder.batchcode.setKeyListener(null);
			if(0 == number) {
				holder.number .setText("�� ��");
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
							 Toast.makeText(JobListActivity.this, "keyCode: " + keyCode, Toast.LENGTH_LONG).show();
							 iSelMaterial = index;
							 String batchcode = holder.batchcode.getText().toString();
							 mDialog.setMessage("����ɨ��������...");
							 mDialog.show();
							 list.get(index).setBatchkeymaterial(batchcode);
							 mListMaterial.get(index).setBatchkeymaterial(batchcode);
							 ThreadData thread = new ThreadData(JobListActivity.this, SCAN_BATCHCODE);
							 thread.start();
							 list.get(index).bEnter = true;
							 return true;
			                }  
			                return false;
					}
				});
				holder.batchcode.addTextChangedListener(new TextWatcher() {
					
					@SuppressLint("WrongConstant")
					@Override
					public void onTextChanged(CharSequence arg0, int start, int before, int count) {
						String str = arg0.toString();
						int Len = str.length();
						//holder.batchcode.gets
						//��holder.batchcode֮ǰ��������count��Ϊ1
						if(1 == count && list.get(index).bEnter) {
							list.get(index).bEnter = false;
							//holder.batchcode.setText(str.substring(start+1, start+2));
							//holder.batchcode.setSelection(1);
							holder.batchcode.setText(str.substring(start, start+1));
							holder.batchcode.setSelection(1);
						}
						if(str.endsWith("\n")){
							Toast.makeText(JobListActivity.this, "start: "+start+"before: "+before+"count: "+count, Toast.LENGTH_LONG).show();
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
			
			holder.material.setText(list.get(position).getMaterial());
			holder.pmgg.setText(list.get(position).getPmgg());
			
			
			String clgkztbz = list.get(position).getClgkztbz();
			if(clgkztbz.equals("1")){
				String materialofcontrol = list.get(position).getMaterialofcontrol();
				//��Ϊ��˵���Ѿ�ɨ��
				if(materialofcontrol.equals("")) holder.material.setBackgroundColor(SelTextColor);
				else holder.material.setBackgroundColor(SelColor);
				
			}else if(clgkztbz.equals("0") && 0 != number){
				holder.material.setBackgroundColor(NONeedSelBackgroundColor);
			}
			
			String batchcode = list.get(position).getBatchkeymaterial();
			holder.batchcode.setText(batchcode);
			//holder.batchcode.setBackgroundColor(Color.TRANSPARENT);
			holder.batchcode.setSelection(batchcode.length());
			if(null == batchcode) list.get(position).bEnter = false;
			else {
				//-------�����Ƿ���Ҫ�ܿ�
				//String strztbz = list.get(position).Getclgkztbz();
				//if(strztbz.equals("1")) view.setBackgroundColor(SelTextColor);
				//else if(strztbz.equals("0") && 0 != number) view.setBackgroundColor(NONeedSelBackgroundColor);
				//-------
				//---------------------------------ɨ���Ϻ���ɲ��ܽ���SOP
				//if(-1 != DatabaseOper.lBarcodes.indexOf(batchcode)) view.setBackgroundColor(SelColor);
				//if(! batchcode.equals("") && 0 != number) view.setBackgroundColor(SelColor);
				//---------------------------------
				
				//���Ƿ������κ�
				String strpcgk = list.get(position).getPcgkztbz();
				if(strpcgk.equals("1")) {
					//��Ϊ��˵���Ѿ�ɨ��
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
