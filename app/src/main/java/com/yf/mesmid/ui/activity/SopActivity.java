package com.yf.mesmid.ui.activity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yf.mesmid.db.DatabaseOper;
import com.yf.mesmid.R;
import com.yf.mesmid.util.ScanSound;
import com.yf.mesmid.entity.SopOQCInfo;
import com.yf.mesmid.entity.SopInfo;

public class SopActivity extends Activity /*implements OnTouchListener*/{
	final String TAG = "MESMID";
	private TextView text_itemname=null;
	final int FLAG_AW_HIDESTATUS = 0x80000000;
	private WebView SOPview = null;
	private ListView mlistview = null;
	private List<Item> mList = new ArrayList<Item>();
	private LinearLayout LinSopInfo;
	private EditText EditSop;
	private TextView Textprocedureinfo;
	private TextView Textjopinfo;
	private TextView Textjopprogress;
	private TextView TextOpertips;
	private ProgressDialog mDialog;
	private Button BtnInJobOrder;
	private Button BtnExit;
	private Button BtnUnwrap;
	private int mHeight;
	private int mWidth;
	private String mbarcode;
	private static String url = "http://blog.sina.com.cn/s/blog_8d955f8c0100yihy.html";
	private boolean bEnter = false;
	private float mPreviousX;
	private float mPreviousY;
	
	private final String ConfigPath = Environment.getExternalStorageDirectory().getPath()+"/MESConfig.ini";
	private String SopFile;
	private String DeviceID;
	private String GD;
	private String gydm = null;
	private String jxbm = null;
	private String batch = null;
	private String moeid = null;
	private String gwdm = null;
	private final int ERROR_NOEXIT = 100;
	private final int CONNECT_SUCCESS = 101;
	private final int SCANBARCODE_SUCCESS = 102;
	private final int SCANPACKQJG_SUCCESS = 103;
	private final int SCANOQC_SUCCESS = 104;
	private final int SCANGETINITIA_SUCCESS = 105;
	private final int SAVEDATE_SUCCESS = 106;
	
	private final int ERRORDIALOG_CANCEL = 120;
	
	private ProgressBar mProgressBar;
	private AlertDialog mErrorDialog;
	private TextView tv_progress;
	
	//user
	//private List<String> LNumber = new ArrayList<String>();
	private AlertDialog UserDialog = null;
	private String User = null;
	
	private final int SCANUSER = 140;
	private final int SCAN_Batch_Code  = 141;
	private final int SCAN_GET_INITIAL  = 142;
	
	private final int MODE_GET_INITIAL  = 233;
	private final int MODE_SAVE_DATE  = 234;
	
	private boolean mDestroy = false;
	
	private boolean bPackQJG = false;
	private List<String> lcldm = new ArrayList<String>();
	private List<String> lbarcode = new ArrayList<String>();
	//-----����ģʽ
	private boolean bReWork = false;
	
	//Ϊ���Ż�������ɨ��Ч��
	private SopInfo mInfo = new SopInfo("", "", "", "", "", "", "", "", "", "", "");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | 
			//	WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | FLAG_AW_HIDESTATUS
				// , WindowManager.LayoutParams.FLAG_FULLSCREEN | 
					//WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | FLAG_AW_HIDESTATUS);
		//requestWindowFeature(getWindow().FEATURE_CUSTOM_TITLE);
		if(DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE) setContentView(R.layout.activity_oqc);
		else setContentView(R.layout.activity_main);
		//getWindow().setFeatureInt(getWindow().FEATURE_CUSTOM_TITLE, R.layout.customtitle);
		mHeight = getWindowManager().getDefaultDisplay().getHeight();
		mWidth = getWindowManager().getDefaultDisplay().getWidth();
		text_itemname = (TextView) findViewById(R.id.text_itemname);
		if(DatabaseOper.SC_MODE==DatabaseOper.ZC_MODE) text_itemname.setText("MESMid ��������");
		else if(DatabaseOper.SC_MODE==DatabaseOper.FG_MODE) text_itemname.setText("MESMid ���ڷ���");
		else if(DatabaseOper.SC_MODE==DatabaseOper.OQC_MODE) text_itemname.setText("OQC ��������");
		BtnInJobOrder = (Button) findViewById(R.id.btn_InJob);
		BtnInJobOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SopActivity.this, JobListActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//���JobListActivity��ջ������JobListActivity
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//������ʷ��JobListActivity��û���򴴽���ʵ��
				//intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				startActivity(intent);
				//finish();
			}
		});
		BtnInJobOrder.setVisibility(View.VISIBLE);
		BtnInJobOrder.setFocusable(false);
		BtnExit = (Button) findViewById(R.id.btn_exit);
		BtnExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {	
				Intent intent = new Intent(SopActivity.this, JobListActivity.class);
				intent.putExtra("exit", true);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				//android.os.Process.killProcess(android.os.Process.myPid());
				//System.exit(0);
				finish();
			}
		});
		BtnUnwrap = (Button) findViewById(R.id.btn_unwrap);//���кŽ��
		BtnUnwrap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				BatchCodeUnwrap(true);
			}
		});
		LinSopInfo = (LinearLayout) findViewById(R.id.ll_body);
		LinSopInfo.setVisibility(View.VISIBLE);
		Textprocedureinfo = (TextView) findViewById(R.id.Text_procedureinfo);
		Textjopinfo = (TextView) findViewById(R.id.Text_jopinfo);
		Textjopprogress = (TextView) findViewById(R.id.Text_jopprogress);
		TextOpertips = (TextView) findViewById(R.id.Text_Opertips);
		EditSop = (EditText) findViewById(R.id.edit_jop);
		//EditSop.setText("S1311000124");
		EditSop.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before, int count) {
				String str = arg0.toString();
				int Len = str.length();
				/*if(Len < 0) {
					String strBuff =  str.substring(Len-2, Len-1);
					if(strBuff.equals("\n")) EditSop.setText(str.substring(Len-1, Len));
				}*/
				if(bEnter){
					bEnter = false;
					if( ! str.endsWith("\n") ) {
						String strBuff = str.substring(Len-1, Len);
						EditSop.setText(strBuff);
						EditSop.setSelection(1);	
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
					
					if(strBuff.equals(MesLsData.QUIT_BARCODE)){
						finish();//�˳��˽���
						System.exit(0);	
					}
					else if(strBuff.equals(MesLsData.INPACKQJG_BARCODE)){//��װǰ�ӹ�ģʽ
						bPackQJG = true;
						//lcldm = MesLsData.lcldm;
						for(int i = 0; i < MesLsData.lcldm.size(); i++)
							lcldm.add(MesLsData.lcldm.get(i));
						TipInfo("�ѽ����װǰ�ӹ�ɨ������ģʽ",  false, 2);
						DisplaySopInfo(true);
						TextOpertips.setText("                "+"������ʾ    \n�ѽ����װǰ�ӹ�ɨ��\n��������ģʽ" );
						TextOpertips.setTextColor(Color.BLACK);
					}else if(strBuff.equals(MesLsData.REWORK_BARCODE)){//��װǰ�ӹ�ģʽ
						bReWork = true;
						TipInfo("�ѽ��뷵��ģʽ",  false, 2);
						DisplaySopInfo(true);
						TextOpertips.setText("                "+"������ʾ    \n�ѽ�����ģʽ" );
						TextOpertips.setTextColor(Color.BLACK);
					}else{
						//Toast.makeText(MainActivity.this, strBuff, 2000).show();
						//mbarcode = strBuff;
						if(DatabaseOper.mbScanUser){
							Intent intent = new Intent(getApplicationContext(), UserActivity.class);
							startActivity(intent);
						}else{
							DatabaseOper.mCount = 0;
							/*�ͻ������У��ģʽ*/
							if(DatabaseOper.ScanMode==DatabaseOper.ScanKHLJCheck)
							{
								if(0 != DatabaseOper.TotalScanNums 
										&& 0 == DatabaseOper.CurScanNums)
								{
									List<String>ListBarcode = DatabaseOper.GetListBarcode();
									/*��Ϊ�ͻ������У��ģʽ��ֻ�ȱ���*/
									if(-1 == ListBarcode.indexOf(strBuff))
									{
										DatabaseOper.CurScanNums++;
										DatabaseOper.AddCLBarcode(strBuff);
                                    	ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
    									TextOpertips.setText("������ʾ\n"+DatabaseOper.DisplayInfo+"\n���к�:"+strBuff+"\n����ɹ�, ��ɨ����һ��У�����к�");
    									TextOpertips.setTextColor(Color.GRAY);
									}
								}
								/*ͨ��CurScanNums�ж�*/
								else if(0 != DatabaseOper.TotalScanNums 
										&& DatabaseOper.CurScanNums == DatabaseOper.TotalScanNums )
								{
									List<String>ListBarcode = DatabaseOper.GetListBarcode();
									if(-1 == ListBarcode.indexOf(strBuff))
									{
										ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
										TextOpertips.setText("������ʾ\n"+DatabaseOper.DisplayInfo+"У��ʧ��, ������ɨ����ˮ�Ų�ȷ��У������");
										TextOpertips.setTextColor(Color.RED);
										BatchCodeUnwrap(false);
										TipError("У��ʧ��, ������ɨ����ˮ�Ų�ȷ��У������", false);
									}else
									{
										/*У��ɹ���CurScanNums�ȼ�1���洢�����л��1*/
										DatabaseOper.CurScanNums--;
										try {
											if(!mDialog.isShowing()){
												mDialog.setMessage("����ɨ������...");
												mDialog.show();
											}
											
										} catch (Exception e) {
											e.printStackTrace();
										}
										RScan rScan = new RScan(strBuff);
										Thread thread = new Thread(rScan);
										thread.start();
									}
									
								}
								else
								{
									try {
										if(!mDialog.isShowing()){
											mDialog.setMessage("����ɨ������...");
											mDialog.show();
										}
										
									} catch (Exception e) {
										e.printStackTrace();
									}
									
									RScan rScan = new RScan(strBuff);
									Thread thread = new Thread(rScan);
									thread.start();
								}
							}
							/*������ӹ�ģʽ*/
							else
							{
								if(0 != DatabaseOper.TotalScanNums 
										&& DatabaseOper.CurScanNums < DatabaseOper.TotalScanNums )
								{
									List<String>ListBarcode = DatabaseOper.GetListBarcode();
									if(-1 == ListBarcode.indexOf(strBuff))
									{
										//DatabaseOper.CurScanNums++;
										DatabaseOper.AddCLBarcode(strBuff);
										/*��Ϊ�ͻ������У��ģʽ��ֻ�ȱ���*/
	                                    if(DatabaseOper.ScanMode==DatabaseOper.ScanKHLJCheck )
	                                    {
	                                    	ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
	    									TextOpertips.setText("������ʾ\n\n"+DatabaseOper.DisplayInfo+"����ɹ�, ��ɨ����һ��У�����к�");
	    									TextOpertips.setTextColor(Color.GRAY);
	                                    }else
	                                    {
	                                    	try {
	                                    		if(!mDialog.isShowing()){
	                                    			mDialog.setMessage("����ɨ������...");
		    										mDialog.show();
	                                    		}
	    										
	    									} catch (Exception e) {
	    										e.printStackTrace();
	    									}
	                                    	RScan rScan = new RScan(strBuff);
	    									Thread thread = new Thread(rScan);
	    									thread.start();
	                                    }
										
										/*if(DatabaseOper.CurScanNums == DatabaseOper.TotalScanNums){
											String strCLBarcode = DatabaseOper.GetCLBarcode();
											mDialog.setMessage("����ɨ������...");
											mDialog.show();
											RScan rScan = new RScan(strCLBarcode);
											Thread thread = new Thread(rScan);
											thread.start();
											DatabaseOper.ResetCLBarcode();
											DatabaseOper.TotalScanNums = 0;
											DatabaseOper.CurScanNums = 0;
										}else{
											ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
											TextOpertips.setText("������ʾ\n\nɨ��ɹ�, ����ɨ����һ������");
											TextOpertips.setTextColor(Color.GRAY);
										}*/
									}else
									{
										ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
										TextOpertips.setText("������ʾ\n\nɨ��ʧ��, �벻Ҫ�ظ�ɨ��");
										TextOpertips.setTextColor(Color.RED);
										TipError("ɨ��ʧ��, �벻Ҫ�ظ�ɨ��", false);
									}
								}
								else{
									try {
										if(!mDialog.isShowing()){
											mDialog.setMessage("����ɨ������...");
											mDialog.show();
										}
										
									} catch (Exception e) {
										e.printStackTrace();
									}
									
									RScan rScan = new RScan(strBuff);
									Thread thread = new Thread(rScan);
									thread.start();
								}
							}
							
						}
					}
					EditSop.setText(str.substring(0, Len-1));
					EditSop.setSelection(Len-1);
					bEnter = true;
				}
				 
				/*if(0 != start && 0 == start%14){
					Toast.makeText(MainActivity.this, str, 2000).show();
					mbarcode = "";
					EditSop.setText("");
				}*/
				//else{mbarcode += str;}
				
				//Toast.makeText(MainActivity.this, "start: "+start+"before: "+before+"count: "+count, 2000).show();
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				//Toast.makeText(MainActivity.this, arg0.toString(), 2000).show();
			}
		});
		EditSop.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean focus) {
				if(focus) {
					int Len = EditSop.getEditableText().toString().length();
					/*SOPview.clearFocus();
					EditSop.setText(EditSop.getEditableText().toString());
					EditSop.setFocusable(true);   
					EditSop.setFocusableInTouchMode(true);
					EditSop.requestFocus();*/
					/*EditSop.requestFocus();
					EditSop.requestFocusFromTouch();*/
					//EditSop.clearFocus();
					//InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
					//imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
					/*EditSop.setFocusable(true);   
					EditSop.setFocusableInTouchMode(true);
					EditSop.requestFocus();
					EditSop.setSelection(Len);*/
				}
			}
		});
		EditSop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int Len = EditSop.getEditableText().toString().length();
				EditSop.setSelection(Len);
			}
		});
		//EditSop.requestFocus();
		//EditSop.setFocusableInTouchMode(false);
		DeviceID = getIntent().getStringExtra("deviceid");
		SopFile = getIntent().getStringExtra("sopfile");
		GD = getIntent().getStringExtra("gd");
		gydm = getIntent().getStringExtra("gydm");
		jxbm = getIntent().getStringExtra("jxbm");
		batch = getIntent().getStringExtra("batch");
		moeid = getIntent().getStringExtra("moeid");
		String strproceduregxmc = getIntent().getStringExtra("proceduregxmc");
		gwdm=getIntent().getStringExtra("procedurexh");
		if(DatabaseOper.SC_MODE == DatabaseOper.OQC_MODE){
			Textprocedureinfo.setText("                    "+"������Ϣ\n��       ��:"+DatabaseOper.User+
					"\n��������:"+strproceduregxmc);
		}else{
			String strprocedurexh = getIntent().getStringExtra("procedurexh");
			Textprocedureinfo.setText("                    "+"������Ϣ\n��       ��:"+strprocedurexh+
					"\n��������:"+strproceduregxmc);
		}
		
		//SopFile = "mes1.htm";
		//SopFile = "PC6953MB50031.htm";
		//int trim = DatabaseOper.Address.indexOf(":", 0);
		//String IP = DatabaseOper.Address.substring(0, trim);

		SOPview = (WebView) findViewById(R.id.textView_sop);
		SOPview.setFocusable(false);
		SOPview.addJavascriptInterface(new JieYunJS(), "jieyunmob");
		
		//SOPview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		//SOPview.clearHistory();
		//SOPview.clearFormData();
		//SOPview.clearCache(true);
		
		
		SOPview.getSettings().setUseWideViewPort(true); 
		SOPview.getSettings().setJavaScriptEnabled(true); 
		SOPview.getSettings().setBuiltInZoomControls(true);//��ʾ�Ŵ���С controler 
		SOPview.getSettings().setSupportZoom(true);
		SOPview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		SOPview.setInitialScale(50);//���ó�ʼ���ű���,��webview��ƥ����Ӧ
		//SOPview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		//SOPview.loadUrl("http://" + IP + "/MesWebService/" + SopFile);
		SOPview.loadUrl(SopFile);
		//SOPview.clearCache(true);
		//SOPview.clearHistory();
		//SOPview.add
		//SOPview.getSettings().setJavaScriptEnabled(true);
		//SOPview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		//SOPview.addJavascriptInterface(new JieYunJS(), "jieyunmob");
		//WebViewClient client = new WebViewClient();
		//SOPview.setWebChromeClient(new WebChromeClient());
		//SOPview.loadUrl("http://docs.google.com/gview?embedded=true&url="
		//		+url);
		
		//���DatabaseOper����
		DatabaseOper.ResetCLBarcode();
		DatabaseOper.TotalScanNums = 0;
		DatabaseOper.CurScanNums = 0;
		
		final String mimetype = "text/html";  
		final String encoding = "utf-8"; 

		//SOPview.loadData("<a href ='http://blog.csdn.net/imyang2007?viewmode=contents'>Young's Blog</a>", mimetype, encoding);
		//SOPview.loadData("http://www.2cto.com/kf/201206/134507.html");
		//SOPview.loadUrl("http://www.2cto.com/kf/201206/134507.html");
		//SOPview.loadUrl("http://192.168.1.103/MesWebService/mes.htm");
		
		//LayoutParams param = (LayoutParams) MainActivity.this.SOPview.getLayoutParams();
		//LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) SOPview.getLayoutParams();
		//param.height = mHeight; param.width = mWidth-100;
		//SOPview.setLayoutParams(param);
		
		mlistview = (ListView) findViewById(R.id.body);
		
		for(int i = 0; i < 10; i++)
		{
			String strValue = "e467887789jjh" + i;
			Item item = new Item("������", strValue);
			mList.add(item);
		}
		
		
		//Thread thread = new Thread(rDB);
		//thread.start();
		mDialog = new ProgressDialog(this);
		//TipUser();
		//-----�涨ʱ����ûɨ������Ҫ����ɨ�蹤��
		Message msg = handler.obtainMessage();
		msg.what = SCANUSER;
		handler.sendMessageDelayed(msg, 1000);
		//-----2Сʱ�л��������б����ɨ�������Ϻ�
		if(DatabaseOper.mbScanBatchCode)
		{
			/*Message msg1 = handler.obtainMessage();
			msg1.what = SCAN_Batch_Code;
			handler.sendMessageDelayed(msg1, 10000);*/
			Thread thread = new Thread(RScanBatchCount);
			thread.start();
		}
		
		//��ȡ��ʼ������
		//mInfo=DatabaseOper.mInfo;
		//Textjopinfo.setText(mInfo.GetjopInfo());
		//Textjopprogress.setText(mInfo.Getjopprogress());
		
		RGetInitial rGetInitial = new RGetInitial(MODE_GET_INITIAL);
		Thread thread = new Thread(rGetInitial);
		thread.start();
		/*Message msg1 = handler.obtainMessage();
		msg1.what = 100004;
		handler.sendMessage(msg1);*/
	}
	
	private void BatchCodeUnwrap(boolean bTipError)
	{
		DatabaseOper.bClInput = false;
		DatabaseOper.TotalScanNums = 0;
		DatabaseOper.CurScanNums = 0;
		DatabaseOper.ResetCLBarcode();
		DatabaseOper.DisplayInfo = "";
		if(bTipError) TipInfo("ϵ�кŽ��ɹ�",  false, 1);
	}
	
	Handler hScanUser = new Handler(){
		public void handleMessage(Message msg) {
			
		};
	};
	
	protected void onDestroy() {
		Log.i(TAG, "SopActivity destroy");
		mDestroy = true;
		//-----ֹͣSOP����
		/*File file = CacheManager.getCacheFileBaseDir(); 
		if (file != null && file.exists() && file.isDirectory()) { 
			for (File item : file.listFiles()) { 
				item.delete(); 
			}
		}
		getApplicationContext().deleteDatabase("webview.db");
		getApplicationContext().deleteDatabase("webviewCache.db");*/
		SOPview.stopLoading();
		super.onDestroy();
	};
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//SOPview.onTouchEvent(ev);
		/*���䷽�������ſؼ�ȥ��
		 * try {
			Field field = SOPview.getClass().getDeclaredField("ZoomButtonsController");
			field.setAccessible(true);
			
			Object zoomControl = field.get("zoomControl");
			field.set("zoomControl", View.GONE);
			field.set(zoomControl, View.GONE);
			zoomControl = View.GONE;
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
        SOPview.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent e) {
				float x = e.getX();
				float y = e.getY();
				//Toast.makeText(getApplicationContext(), "x: "+x+" y: "+y+" mPreviousX: "+mPreviousX+" mPreviousY: "+mPreviousY, 2000).show();
				switch (e.getAction()) {
				case MotionEvent.ACTION_MOVE:
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				if (dx > 80) {
					LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) SopActivity.this.SOPview.getLayoutParams();
					//param.height = mHeight; 
					param.width = mWidth;
					
				    SOPview.setLayoutParams(param);
					MESAdapt adapt = new MESAdapt(SopActivity.this, mList);
					mlistview.setAdapter(adapt);
					LinSopInfo.setVisibility(View.GONE);
				}
				else if (dx < -80) {
					LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) SopActivity.this.SOPview.getLayoutParams();
					//param.height = mHeight; 
					param.width = mWidth-320;
					
				    SOPview.setLayoutParams(param);
					MESAdapt adapt = new MESAdapt(SopActivity.this, mList);
					mlistview.setAdapter(adapt);
					LinSopInfo.setVisibility(View.VISIBLE);
				}
				break;
				}
				mPreviousX = x;
				mPreviousY = y;
				return false;
			}
		});
		//Toast.makeText(getApplicationContext(), "dispatchTouchEvent", 3000).show();
		return super.dispatchTouchEvent(ev);
	}
	
	/*Handler handle = new Handler(){
		public void handleMessage(Message msg) {};
	};*/
	
	boolean CheckQJG(String strtm)
	{
		if(strtm == null || 7 >= strtm.length()) return false;
		if("QJG" == strtm.substring(0, 3)) return true;
		else return false;
	}
	
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
			ScanResult result = null;
			/*OQCɨ��ģʽ*/
			if(DatabaseOper.SC_MODE == DatabaseOper.OQC_MODE){
				result = DatabaseOper.ScanBarcodeOQC(DeviceID, barcode, GD);
				if(null == result){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, "ɨ������ʧ��", 0);
				}
				else{
					SendDataMessage(SCANOQC_SUCCESS, result, 0);
				}
			}
			else
			{
				if(DatabaseOper.ScanMode == DatabaseOper.ScanSN || DatabaseOper.ScanMode == DatabaseOper.ScanKHLJCheck) {//ɨ����ˮ��ģʽ
					/*��װǰ�ӹ�ģʽ*/
					if(bPackQJG){
						String wldm = DatabaseOper.SpGetWldm(barcode);
						if(-1 != lcldm.indexOf(wldm)) {
							lcldm.remove(wldm);
							lbarcode.add(barcode);
							SendDataMessage(SCANPACKQJG_SUCCESS, 
									"ɨ������ɹ�, "+"��ɨ��"+(MesLsData.lcldm.size()-lcldm.size())+"��, "+"����ɨ��"+lcldm.size()+"��, ��ɨ����һ��", 0);
							ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
						}
						else{
							SendDataMessage(ERROR_NOEXIT, "��������ɨ����ڱ���λ, ������ɨ��", 0);
							ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
						}
						int count = lcldm.size();
						if(0 == count) {
							String tolwltm = "";
							for(int i = 0; i < lbarcode.size(); i++)
							{
								if(i == lbarcode.size() - 1) tolwltm += lbarcode.get(i);
								else tolwltm += lbarcode.get(i) + ";";
							}	
							result = DatabaseOper.ScanQJG(DeviceID, tolwltm, ""+lbarcode.size());
							
						}
						else return;
					}
					else{
						if(CheckQJG(barcode)){
							String Code = barcode.substring(0, barcode.length()-1);
							String SL = barcode.substring(barcode.length()-1, barcode.length());
							result = DatabaseOper.ScanQJG(DeviceID, Code, SL);
						}else{
							if(CheckBLTM(barcode)) DatabaseOper.mbBL = true;
							else DatabaseOper.mbBL = false;
							//result = DatabaseOper.ScanBarcode(DeviceID, barcode);
							//���뷵��ģʽ
							if(bReWork) result = DatabaseOper.ScanBarcodeEx(DeviceID, barcode, "1");
							else result = DatabaseOper.ScanBarcodeEx(DeviceID, barcode, "0");
						}
						
					}
					
				}
				else if(DatabaseOper.ScanMode == DatabaseOper.ScanGD) {//AOI TOP��������ģʽ
					int Len = barcode.length();
					if(Len < 3) result = null;
					else {
						String jgtm = barcode.substring(0, 2);
						String number = barcode.substring(2);
						if(!"ng".equals(jgtm) && !"NG".equals(jgtm) &&
								!"ok".equals(jgtm) && !"OK".equals(jgtm)){
							 Len = DatabaseOper.SNCode.length();
							 //if(Len < 3) result = null;
							 //if(barcode.length() != 4) result = null;
							 if(Len < 3 || barcode.length() != 4) result = null;
							 else{
								 jgtm = DatabaseOper.SNCode.substring(0, 2);
								 number = DatabaseOper.SNCode.substring(2);
								 result = DatabaseOper.ScanSN(DeviceID, GD, jgtm, number, barcode);
							 }
							
						}
						else result = DatabaseOper.ScanSN(DeviceID, GD, jgtm, number, "");
					}
					
				}
				else if(DatabaseOper.ScanMode == DatabaseOper.ScanSMTAOI){//ɨ��SMT AOI����ģʽ
					result = DatabaseOper.ScanSMTAOI(DeviceID, barcode);
				}
				//String result =DatabaseOper.ScanSMTInfo(barcode, "0", "1");
				if(null == result){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, "ɨ������ʧ��", 0);
				}
				else{
					SendDataMessage(SCANBARCODE_SUCCESS, result, 0);
				}
			}
			
			
		}
		
	}
	
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
				DatabaseOper.InitDatabaseConfig(ConfigPath);
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(ERROR_NOEXIT, "���ݿ�����ʧ��", 0);
					return;
				}else SendDataMessage(CONNECT_SUCCESS, "���ݿ����ӳɹ�������ɨ������...", 0);
			}
			if(this.Mode==MODE_GET_INITIAL){
				SopInfo info = null;
				info = DatabaseOper.GetInitial(DeviceID, gydm, moeid,gwdm);
				if(null == info){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, "��ȡ��ʼ����ʧ��", 0);
				}
				else{
					SendDataMessage(SCANGETINITIA_SUCCESS, info, 0);
				}
			}
			else if(this.Mode==MODE_SAVE_DATE){
				boolean bRet = DatabaseOper.SaveDate(mInfo.Getoutputnums(), mInfo.Getacceptednums(), moeid, gwdm);
				if(false == bRet){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SendDataMessage(ERROR_NOEXIT, "��������ʧ��", 0);
				}
				else{
					SendDataMessage(SAVEDATE_SUCCESS, "�������ʳɹ�", 0);
				}
			}	
			
		}
		
	}
	
	public void GetData(Connection con, String barcode) throws java.sql.SQLException {

		try {

			//String sql = "SELECT * FROM bin_mstr";//��ѯ����Ϊ��table_test������������
			/*String sql = "declare @Ret int declare	" +
					"@RetMsg nvarchar(200) declare	@wjm nvarchar(200) " +
					"declare	@gdxx nvarchar(200) 	declare	@smsl int 	" +
					"exec [data_deal_op] 'A01','A1311000105','',''" +
					",'J1300000010','ADMIN',@Ret output,@RetMsg output,@wjm output,@gdxx output," +
					"@smsl output select @Ret as xh,@RetMsg as info,@wjm as filename,@gdxx as moinfo,@smsl as sl";*/
			String sql = "";
			sql=sql+"truncate table tem_mstr \n";
			sql=sql+"truncate table op100_det \n";
			sql=sql+"declare @Ret int ";
			sql=sql+"declare @RetMsg nvarchar(200) ";
			sql=sql+"declare @RetInfo nvarchar(500) ";
			sql=sql+"declare @RetFile nvarchar(500) ";
			sql=sql+"exec data_deal_op_yf 'DIP8',";
			sql=sql+ "'" + barcode+ "'"; //"'S1311000122'";
			sql=sql+",'','','ADMIN',@Ret output ,@RetMsg output,@RetInfo output,@RetFile output ";
			sql=sql+"select @Ret as xh ,@RetMsg as oprmsg ,@RetInfo as moinfo,@RetFile as SopFile ";
	
			Statement stmt = con.createStatement();//����Statement
			//URL url = new URL()
			
			ResultSet rs = stmt.executeQuery(sql);//ResultSet����Cursor

			if (rs.next()) {//<code>ResultSet</code>���ָ���һ��
				String strxh = rs.getString("xh");
				String strmsg= rs.getString("oprmsg");
				String strSOPPath = rs.getString("SopFile");
				String strmoInfo = rs.getString("moinfo");
				//String strSL = rs.getString("sl");
				
				Log.i(TAG, "xh: " + strxh);
				Log.i(TAG, "info: " + strmsg);
				Log.i(TAG, "filename: " + strSOPPath);
				Log.i(TAG, "moinfo: " + strmoInfo);
				//Log.i(TAG, "sl: " + strSL);
				ScanResult result = new ScanResult(strxh, strmsg, strmoInfo, strSOPPath);
				SendDataMessage(SCANBARCODE_SUCCESS, result, 0);
				//ExcelOper.readExcel("/mnt/sdcard/WC-BA12-012 CP6953-KA 1.1 ��װ��ҵָ�� .xls");
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage().toString());
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
				}
		}
	}
	
	SopInfo ParsemoInfo(String moInfo, String Opertips)
	{
		if(null == moInfo) {
			SopInfo info = new SopInfo("", "", "", "", "", "", 
					"", "", "", "", Opertips);
			return info;
		}
		int Len = moInfo.length();
		String jopnumber=""; String model=""; String PN="";
		String batch=""; String outputnums=""; String acceptednums="";
		String badnums=""; String passrate=""; String totaloutputs=""; String acceptedrate="";
		int i = 0; int iSPos = 0;
		int iEPos = moInfo.indexOf(";"); 
		//if(-1 != iPos) ;
		while(-1 != iEPos)
		{
			if(0 == i) jopnumber = moInfo.substring(iSPos, iEPos);
			else if(1 == i) model = moInfo.substring(iSPos, iEPos);
			else if(2 == i) PN = moInfo.substring(iSPos, iEPos);
			else if(3 == i) batch = moInfo.substring(iSPos, iEPos);
			else if(4 == i) outputnums = moInfo.substring(iSPos, iEPos);
			else if(5 == i) acceptednums = moInfo.substring(iSPos, iEPos);
			else if(6 == i) badnums = moInfo.substring(iSPos, iEPos);
			else if(7 == i) passrate = moInfo.substring(iSPos, iEPos);
			else if(8 == i) totaloutputs = moInfo.substring(iSPos, iEPos);
			else if(9 == i) acceptedrate = moInfo.substring(iSPos, iEPos);
			iSPos = iEPos + 1; 
			if(iSPos >= Len) break;
			iEPos = moInfo.indexOf(";", iSPos);
			if(-1 == iEPos) iEPos = Len;
			i++;
		}
		//ͨ������������
		//SopInfo info = new SopInfo(jopnumber, model, PN, batch, outputnums, acceptednums, 
				//badnums, passrate, totaloutputs, acceptedrate, Opertips);
		//ͨ��ƽ�����
		SopInfo info = new SopInfo(jopnumber, model, PN, batch, mInfo.Getoutputnums(), mInfo.Getacceptednums(), 
				mInfo.Getbadnums(), mInfo.Getpassrate(), mInfo.Gettotaloutputs(), mInfo.Getacceptedrate(), Opertips);
		return info;
	}
	
	SopOQCInfo ParsemoOQCInfo(String moInfo, String Opertips)
	{
		if(null == moInfo) {
			SopOQCInfo info = new SopOQCInfo("", "", "", "", "", "", 
					"", "", "", Opertips);
			return info;
		}
		int Len = moInfo.length();
		String cjnumber=""; String wldm=""; String AQL="";
		String sjbatch=""; String cjnums=""; String yjnums="";
		String lpnums=""; String badnums=""; String passrate=""; 
		int i = 0; int iSPos = 0;
		int iEPos = moInfo.indexOf(";"); 
		//if(-1 != iPos) ;
		while(-1 != iEPos)
		{
			if(0 == i) cjnumber = moInfo.substring(iSPos, iEPos);
			else if(1 == i) wldm = moInfo.substring(iSPos, iEPos);
			else if(2 == i) AQL = moInfo.substring(iSPos, iEPos);
			else if(3 == i) sjbatch = moInfo.substring(iSPos, iEPos);
			else if(4 == i) cjnums = moInfo.substring(iSPos, iEPos);
			else if(5 == i) yjnums = moInfo.substring(iSPos, iEPos);
			else if(6 == i) lpnums = moInfo.substring(iSPos, iEPos);
			else if(7 == i) badnums = moInfo.substring(iSPos, iEPos);
			else if(8 == i) passrate = moInfo.substring(iSPos, iEPos);
			iSPos = iEPos + 1; 
			if(iSPos >= Len) break;
			iEPos = moInfo.indexOf(";", iSPos);
			if(-1 == iEPos) iEPos = Len;
			i++;
		}
		SopOQCInfo info = new SopOQCInfo(cjnumber, wldm, AQL, sjbatch, cjnums, yjnums, 
				 lpnums, badnums, passrate, Opertips);
		return info;
	}
	
	private void DisplaySopInfo(boolean bRet)
	{
		LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) SopActivity.this.SOPview.getLayoutParams();
		if(bRet){
			param.width = mWidth-320;
			LinSopInfo.setVisibility(View.VISIBLE);
		}
		else{
			param.width = mWidth;
			LinSopInfo.setVisibility(View.GONE);
		}
		SOPview.setLayoutParams(param);
		MESAdapt adapt = new MESAdapt(SopActivity.this, mList);
		mlistview.setAdapter(adapt);
	}
	
	private void TipError(String strInfo, final boolean bExit)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(SopActivity.this);
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
	
	private void TipInfo(String strInfo, final boolean bExit, int time)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(SopActivity.this);
		build.setTitle("��ʾ                              \n\n\n\n");
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
		SendDataMessage(ERRORDIALOG_CANCEL, "����Ի�����ʧ", time);
	}
	
	private boolean CheckUser(String strUser)
	{
		if(null == strUser) return false;
		for(int i = 0; i < strUser.length(); i++)
		{
			String strNum = strUser.substring(i, i+1);
			int iNum = Integer.valueOf(strNum).intValue();
			if(iNum < 0 || iNum > 9) return false;
		}
		return true;
	}
	
	private void TipUser()
	{
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.user, null);
		final EditText EditUser = (EditText) view.findViewById(R.id.edituser);
		EditUser.setFocusable(true);
		EditSop.setFocusable(false);
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
						User = strBuff;
						UserDialog.cancel();
						EditUser.setFocusable(false);
						EditSop.setFocusable(true);
					}
				}	 
		
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {}
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
		AlertDialog.Builder build = new AlertDialog.Builder(SopActivity.this);
		build.setTitle("�������û���");
		//build.setMessage("�������û���");
		build.setView(view);
		UserDialog = build.create();
		UserDialog.setCanceledOnTouchOutside(false);
		UserDialog.show();
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
				try {
					if(mDialog.isShowing()){
						mDialog.cancel();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				TipError((String)msg.obj, false);
			}
			else if(Code == CONNECT_SUCCESS){
				try {
					mDialog.setMessage((String)msg.obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			else if(Code == SCANPACKQJG_SUCCESS){
				try {
					if(mDialog.isShowing()){
						mDialog.setMessage("ɨ������ɹ�");
						mDialog.cancel();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				DisplaySopInfo(true);
				String strInfo = "                " +"������ʾ    \n"+(String)msg.obj+"(��װǰ�ӹ�ģʽ)"; 
				String strPre = "                " +"������ʾ    \n"; String strDisplay = strPre;
				for(int i = strDisplay.length(); i < strInfo.length(); i++)
				{
					strDisplay += strInfo.substring(i, i+1);
					if(0 != (i-12) && 0 == ((i-12)%12)) strDisplay += "\n";
				}
				TextOpertips.setText(strDisplay);
				TextOpertips.setTextColor(Color.BLACK);
			}
			/*��ȡSOP�������*/
			else if(Code == SCANBARCODE_SUCCESS){
				try {
					if(mDialog.isShowing()){
						mDialog.setMessage("ɨ������ɹ�");
						mDialog.cancel();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				DisplaySopInfo(true);
				ScanResult result = (ScanResult) msg.obj;
				String indexback = result.Getindexback();
				int index = Integer.valueOf(indexback).intValue();
				if(index < 100 || 103 == index){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SopInfo info = new SopInfo();
					info.SetOperTips(result.Getinfoback());
					String strInfo = info.GetTolOpertips(); String strPre = "                " +"������ʾ    \n"; 
					String strDisplay = strInfo;
					/*String strDisplay = strPre;
					for(int i = strDisplay.length(); i < strInfo.length(); i++)
					{
						strDisplay += strInfo.substring(i, i+1);
						if(0 != (i-12) && 0 == ((i-12)%12)) strDisplay += "\n";
					}*/
					TextOpertips.setText(strDisplay+DatabaseOper.DisplayInfo);
					//TextOpertips.setText(strDisplay);//��Ϣ��ʾ
					TextOpertips.setTextColor(Color.RED);
					if(0 != DatabaseOper.TotalScanNums 
							&& DatabaseOper.CurScanNums == DatabaseOper.TotalScanNums ) DatabaseOper.DisplayInfo="";//��Ϣ��ʾ
					TipError(info.GetTolOpertips().substring(strPre.length()), false);
				}
				else{
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
					if(DatabaseOper.mbBL && !DatabaseOper.bClInput) mInfo.SetFail(DatabaseOper.mljNums);
					else if(!DatabaseOper.mbBL && !DatabaseOper.bClInput) mInfo.SetPass(DatabaseOper.mljNums);
					String moInfo = result.Getmoinfo();
					String Opertips = result.Getinfoback();
					SopInfo info = ParsemoInfo(moInfo, Opertips);
					Textjopinfo.setText(info.GetjopInfo());
					Textjopprogress.setText(info.Getjopprogress());
					
					String strInfo = info.GetTolOpertips(); if(bPackQJG) strInfo +="(��װǰ�ӹ�ģʽ)";
					if(bReWork) strInfo +="(����ģʽ)";
					String strPre = "                " +"������ʾ    \n"; 
					String strDisplay = strInfo;
					/*String strDisplay = strPre;
					for(int i = strDisplay.length(); i < strInfo.length(); i++)
					{
						strDisplay += strInfo.substring(i, i+1);
						if(0 != (i-12) && 0 == ((i-12)%12)) strDisplay += "\n";///------------
					}*/
					//TextOpertips.setText(strDisplay);//��Ϣ��ʾ
					TextOpertips.setText(strDisplay+DatabaseOper.DisplayInfo);
					TextOpertips.setTextColor(Color.BLACK);
					if(0 != DatabaseOper.TotalScanNums 
							&& DatabaseOper.CurScanNums == DatabaseOper.TotalScanNums ) DatabaseOper.DisplayInfo="";//��Ϣ��ʾ
					String filename = result.Getfilename();
					if( ! "".equals(filename) && null != filename && ! filename.equals(SopFile) ){
						SopFile = filename;
						int trim = DatabaseOper.Address.indexOf(":", 0);
						String IP = DatabaseOper.Address.substring(0, trim);
						//SOPview.loadUrl("http://" + IP + "/MesWebService/" + SopFile);
						SOPview.loadUrl(SopFile);
					}
					
					//��װǰ�ӹ�ģʽ�ָ�
					//lcldm = MesLsData.lcldm;
					for(int i = 0; i < MesLsData.lcldm.size(); i++)
						lcldm.add(MesLsData.lcldm.get(i));
					lbarcode.clear();
					
					//������������
					if(!DatabaseOper.bClInput){
						RGetInitial rGetInitial = new RGetInitial(MODE_SAVE_DATE);
						Thread thread = new Thread(rGetInitial);
						thread.start();
					}
					
					//if(102 == index) 
					//SOPview.loadUrl("http://192.168.1.103/MesWebService/" + "PCBA��ҵָ��.mht");
					//SOPview.loadUrl("http://192.168.1.103/MesWebService/" + "mes.htm");
					//SOPview.loadUrl(url);
				}
				//String 
				//BtnInJobOrder.setVisibility(View.VISIBLE);
				//EditSop.setVisibility(View.GONE);
				//LinSopInfo.setVisibility(View.VISIBLE);
				//SOPview.setVisibility(View.VISIBLE);
			}
			/*ɨ��OQC�������*/
			else if(Code == SCANOQC_SUCCESS){
				try {
					if(mDialog.isShowing()){
						mDialog.setMessage("ɨ������ɹ�");
						mDialog.cancel();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				DisplaySopInfo(true);
				ScanResult result = (ScanResult) msg.obj;
				String indexback = result.Getindexback();
				int index = Integer.valueOf(indexback).intValue();
				if(index < 100 || 103 == index){
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
					SopInfo info = new SopInfo();
					info.SetOperTips(result.Getinfoback());
					String strInfo = info.GetTolOpertips(); String strPre = "                " +"������ʾ    \n"; 
					String strDisplay = strInfo;
					TextOpertips.setText(strDisplay+DatabaseOper.DisplayInfo);
					//TextOpertips.setText(strDisplay);//��Ϣ��ʾ
					TextOpertips.setTextColor(Color.RED);
					TipError(info.GetTolOpertips().substring(strPre.length()), false);
				}
				else{
					ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
					String moInfo = result.Getmoinfo();
					String Opertips = result.Getinfoback();
					SopOQCInfo info = ParsemoOQCInfo(moInfo, Opertips);
					Textjopinfo.setText(info.GetjopInfo());
					Textjopprogress.setText(info.Getjopprogress());
					
					String strInfo = info.GetTolOpertips();
					String strPre = "                " +"������ʾ    \n"; 
					String strDisplay = strInfo;
					TextOpertips.setText(strDisplay+DatabaseOper.DisplayInfo);
					TextOpertips.setTextColor(Color.BLACK);
					String filename = result.Getfilename();
					if( ! "".equals(filename) && null != filename && ! filename.equals(SopFile) ){
						SopFile = filename;
						int trim = DatabaseOper.Address.indexOf(":", 0);
						String IP = DatabaseOper.Address.substring(0, trim);
						SOPview.loadUrl(SopFile);
					}
					
				}
			}
			/*�õ���ʼ�������*/
			else if(Code == SCANGETINITIA_SUCCESS){
				try {
					if(mDialog.isShowing()){
						mDialog.setMessage("��ȡ��ʼ���ݳɹ�");
						mDialog.cancel();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				mInfo= (SopInfo) msg.obj;
				mInfo.Setmodel(jxbm);
				mInfo.Setjopnumber(GD);
				mInfo.Setbatch(batch);
				mInfo.SetPN(gydm);
				Textjopinfo.setText(mInfo.GetjopInfo());
				Textjopprogress.setText(mInfo.Getjopprogress());
				
			}
			/*�õ���ʼ�������*/
			else if(Code == SAVEDATE_SUCCESS){
				try {
					if(mDialog.isShowing()){
						mDialog.setMessage((String)msg.obj);
						mDialog.cancel();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			else if(ERRORDIALOG_CANCEL == Code){
				mErrorDialog.cancel();
			}
			else if(SCANUSER == Code){
				
				if(DatabaseOper.mCount == DatabaseOper.mScanUserTime){
					DatabaseOper.mbScanUser = true;
					Intent intent = new Intent(getApplicationContext(), UserActivity.class);
					startActivity(intent);
					DatabaseOper.mCount++;
				}else if(DatabaseOper.mCount  < DatabaseOper.mScanUserTime){
					DatabaseOper.mCount++;
					Log.i(TAG, "ScanUserCount: "+DatabaseOper.mCount);
				}
				if(! mDestroy){
					Message msg1 = handler.obtainMessage();
					msg1.what = SCANUSER;
					handler.sendMessageDelayed(msg1, 1000);	
				}
			}
			else if(SCAN_Batch_Code == Code){
				DatabaseOper.mBatchCodeCount++;
				if(DatabaseOper.mBatchCodeCount == DatabaseOper.mScanBatchCodeTime){
					Intent intent = new Intent(SopActivity.this, JobListActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//���JobListActivity��ջ������JobListActivity
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//������ʷ��JobListActivity��û���򴴽���ʵ��
					intent.putExtra("scanbatchcode", true);
					startActivity(intent);
				}else{
					if((! mDestroy)){
						Message msg1 = handler.obtainMessage();
						msg1.what = SCAN_Batch_Code;
						handler.sendMessageDelayed(msg1, 10000);	
					}
					
				}
				Log.i(TAG, "BatchCodeCount: "+DatabaseOper.mBatchCodeCount);
			}else if(100004==Code){
				RGetInitial rGetInitial = new RGetInitial(MODE_GET_INITIAL);
				Thread thread = new Thread(rGetInitial);
				thread.start();
				Message msg1 = handler.obtainMessage();
			}
		};
	};
	
	/*��Сʱ����ɨ���߳�*/
	Runnable RScanBatchCount = new Runnable() {
		
		@Override
		public void run() {
			while(! mDestroy)
			{
				DatabaseOper.mBatchCodeCount++;
				if(DatabaseOper.mBatchCodeCount == DatabaseOper.mScanBatchCodeTime){
					Intent intent = new Intent(SopActivity.this, JobListActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//���JobListActivity��ջ������JobListActivity
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//������ʷ��JobListActivity��û���򴴽���ʵ��
					intent.putExtra("scanbatchcode", true);
					startActivity(intent);
					System.exit(0);
					finish();
				}else{
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Log.i(TAG, "BatchCodeCount: "+DatabaseOper.mBatchCodeCount);
			}
			
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
		//return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_items:
			/*LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) MainActivity.this.SOPview.getLayoutParams();
			param.height = mHeight; param.width = mWidth-300;
			
		    SOPview.setLayoutParams(param);
			MESAdapt adapt = new MESAdapt(this, mList);
			mlistview.setAdapter(adapt);
			LinSopInfo.setVisibility(View.VISIBLE);*/
			Intent intent = new Intent(SopActivity.this, JobListActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int k = keyCode;
		String str = event.getCharacters();
		//Toast.makeText(this, str, 2000).show();
		return super.onKeyDown(keyCode, event);
	}
	
	class Item{
		private String name;
		private String value;
		
		Item(String strName, String strValue)
		{
			this.name = strName;
			this.value = strValue;
		}
		
		void SetName(String strName){
			this.name = strName;
		}
		
		void SetValue(String strValue){
			this.value = strValue;
		}
		
		String GetName(){
			return this.name;
		}
		
		String GetValue(){
			return this.value;
		}
	}
	
	private static class ViewHolder {
        TextView name;
        EditText value;
	}
	
	private class MESAdapt extends BaseAdapter{
		Context context;
		List<Item> list;
		
		MESAdapt(Context context, List<Item> List){
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
			View view = inflater.inflate(R.layout.item, null);
			ViewHolder holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.textItem);
			holder.value = (EditText) view.findViewById(R.id.editItem);
			holder.name .setText(list.get(position).GetName());
			holder.value.setText(list.get(position).GetValue());
			return view;
		}
		
	}

	/*@Override
	public boolean onTouch(View v, MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
		float dx = x - mPreviousX;
		float dy = y - mPreviousY;
		if (dx > 80) {
			LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) MainActivity.this.SOPview.getLayoutParams();
			//param.height = mHeight; 
			param.width = mWidth;
			
		    SOPview.setLayoutParams(param);
			MESAdapt adapt = new MESAdapt(MainActivity.this, mList);
			mlistview.setAdapter(adapt);
			LinSopInfo.setVisibility(View.GONE);
		}
		else if (dx < -80) {
			LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) MainActivity.this.SOPview.getLayoutParams();
			//param.height = mHeight; 
			param.width = mWidth-300;
			
		    SOPview.setLayoutParams(param);
			MESAdapt adapt = new MESAdapt(MainActivity.this, mList);
			mlistview.setAdapter(adapt);
			LinSopInfo.setVisibility(View.VISIBLE);
		}
		break;
		}
		mPreviousX = x;
		mPreviousY = y;
		return false;
	}*/
	
	private ICallbackResult callback = new ICallbackResult() {

		@Override
		public void OnBackResult(Object result) {
			// TODO Auto-generated method stub
			if ("finish".equals(result)) {
				finish();
				return;
			}
			
			int i = (Integer) result;
			mProgressBar.setProgress(i);
			// tv_progress.setText("��ǰ���� =>  "+i+"%");
			// tv_progress.postInvalidate();
			mHandler.sendEmptyMessage(i);
		}

	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_progress.setText("��ǰ���� �� " + msg.what + "%");

		};
	};
	
	public interface ICallbackResult {
		public void OnBackResult(Object result);
	}
}
