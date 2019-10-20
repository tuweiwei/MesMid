package com.yf.mesmid.ui.activitys.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.yf.mesmid.R;

public class SopYuLangActivity extends Activity /*implements OnTouchListener*/{
	final String TAG = "MESMID";
	final int FLAG_AW_HIDESTATUS = 0x80000000;
	private WebView SOPview = null;
	private String SopFile;
	private String DeviceID;
	private String GD;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | 
			//	WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | FLAG_AW_HIDESTATUS
				// , WindowManager.LayoutParams.FLAG_FULLSCREEN | 
					//WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | FLAG_AW_HIDESTATUS);
		//requestWindowFeature(getWindow().FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.sopyulang);
		//getWindow().setFeatureInt(getWindow().FEATURE_CUSTOM_TITLE, R.layout.customtitle);
		
		DeviceID = getIntent().getStringExtra("deviceid");
		SopFile = getIntent().getStringExtra("sopfile");
		GD = getIntent().getStringExtra("gd");
		SOPview = (WebView) findViewById(R.id.textView_sop);
		SOPview.setFocusable(false);
		SOPview.addJavascriptInterface(new JieYunJS(), "jieyunmob");
		SOPview.getSettings().setUseWideViewPort(true); 
		SOPview.getSettings().setJavaScriptEnabled(true); 
		SOPview.getSettings().setBuiltInZoomControls(true);//��ʾ�Ŵ���С controler 
		SOPview.getSettings().setSupportZoom(true);
		SOPview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		SOPview.setInitialScale(50);//���ó�ʼ���ű���,��webview��ƥ����Ӧ
		SOPview.loadUrl(SopFile);
		/*5���ӽ���SOPԤ��*/
		MesLsData.YuLangTimeCount = 0;
		Message msg = handler.obtainMessage();
		msg.arg1 = 101;
		handler.sendMessageDelayed(msg, 1000);
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if(101 == msg.arg1){
				MesLsData.YuLangTimeCount++;
				if(MesLsData.YuLangTimeCount == MesLsData.YuLangTime){finish();}
				else{
					Message msg1 = handler.obtainMessage();
					msg1.arg1 = 101;
					handler.sendMessageDelayed(msg1, 1000);
				}
			}
		};
	};
	
	protected void onDestroy() {
		Log.i(TAG, "SopYuLangActivity destroy");
		super.onDestroy();
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
			Intent intent = new Intent(SopYuLangActivity.this, JobListActivity.class);
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
}
