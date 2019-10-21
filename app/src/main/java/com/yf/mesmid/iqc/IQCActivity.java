package com.yf.mesmid.iqc;

import com.yf.mesmid.barcodebind.UserActivity;
import com.yf.mesmid.consts.MyConsts;
import com.yf.mesmid.db.DatabaseOper;
import com.yf.mesmid.R;
import com.yf.mesmid.util.ScanSound;
import com.yf.mesmid.entity.IOQCInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class IQCActivity extends Activity{
	private ProgressDialog mDialog;
	private TextView TextOpertips = null;
	private WebView SOPview = null;
	private boolean bEnter = false;
	private String DeviceID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.iqc);
		mDialog = new ProgressDialog(this);
		TextOpertips = (TextView) findViewById(R.id.Text_iqcinfo);
		final EditText Editopc = (EditText) findViewById(R.id.edit_iqc);
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
		DeviceID = getIntent().getStringExtra("deviceid");
		SOPview = (WebView) findViewById(R.id.textView_sop);
		SOPview.setFocusable(false);
		//SOPview.addJavascriptInterface(new JieYunJS(), "jieyunmob");
		SOPview.getSettings().setUseWideViewPort(true); 
		SOPview.getSettings().setJavaScriptEnabled(true); 
		SOPview.getSettings().setBuiltInZoomControls(true);
		SOPview.getSettings().setSupportZoom(true);
		SOPview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		SOPview.setInitialScale(50);
		//SOPview.loadUrl("http://192.168.29.3/UploadWLSOP/2014-12-11 154542/index.html");
		//SOPview.setVisibility(View.VISIBLE);
		Intent intent = new Intent(getApplicationContext(), UserActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	private void SendDataMessage(int Code, Object Data, int delay){
		Message msg = handler.obtainMessage();
		msg.what = Code;
		msg.obj = Data;
		handler.sendMessageDelayed(msg, delay*1000);
	}
	
	Handler handler = new MySafeHandler(this);

	private static class MySafeHandler extends Handler{
		private WeakReference<IQCActivity> ref;

		public MySafeHandler(IQCActivity ref){
			this.ref = new WeakReference<IQCActivity>(ref);
		}

		@Override
		public void handleMessage(Message msg) {
			int Code = msg.what;
			IQCActivity activity = ref.get();
			if(Code == MyConsts.ERROR_NOEXIT){
				IOQCInfo info = (IOQCInfo)msg.obj;
				activity.mDialog.cancel();
				activity.TextOpertips.setTextColor(Color.RED);
				//TextOpertips.setText(info.Getinfo());
				activity.TextOpertips.setVisibility(View.VISIBLE);
				activity.SOPview.setVisibility(View.GONE);
			}
			else if(Code == MyConsts.CONNECT_SUCCESS){
				activity.mDialog.setMessage((String)msg.obj);
			}

			else if(Code == MyConsts.SCANBARCODE_SUCCESS){
				IOQCInfo info = (IOQCInfo)msg.obj;
				//mDialog.setMessage(info.Getinfo());
				activity.mDialog.cancel();
				activity.TextOpertips.setTextColor(Color.BLUE);
				activity.TextOpertips.setText("ɨ��ɹ�");
				activity.TextOpertips.setVisibility(View.GONE);
				int itrim = DatabaseOper.Address.indexOf(":", 0);
				String IP = DatabaseOper.Address.substring(0, itrim);
				//SOPview.loadUrl("http://"+IP+info.Getinfo());
				//SOPview.loadUrl(info.Getinfo());
				//SOPview.loadUrl("http://192.168.29.3/UploadWLSOP/2014-12-11 154542/index.html");
				activity.SOPview.setVisibility(View.VISIBLE);
			}
		}
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
				//DatabaseOper.InitDatabaseConfig(MyConsts.ConfigPath);
				if ( ! DatabaseOper.Connect() ) {
					SendDataMessage(MyConsts.ERROR_NOEXIT, "���ݿ�����ʧ��", 0);
					return;
				}else SendDataMessage(MyConsts.CONNECT_SUCCESS, "���ݿ����ӳɹ�������ɨ������...", 0);
			}
			
			IOQCInfo info =DatabaseOper.ScanIQCBarcode(barcode, DeviceID);
			if(! info.getXh().equals("100")){
				ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_ERROR);
				SendDataMessage(MyConsts.ERROR_NOEXIT, info, 0);
			}
			else{
				ScanSound.PlayMusic(getApplicationContext(), ScanSound.MUSIC_RIGHT);
				SendDataMessage(MyConsts.SCANBARCODE_SUCCESS, info, 0);
			}
			
		}
	}
}
