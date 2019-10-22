package com.yf.mesmid.app;

import com.yf.mesmid.R;
import com.yf.mesmid.service.WifiService;
import com.yf.mesmid.tid.activity.XGActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author tuwei
 *
 * /**
 *  * [Config]
 *  * connmode=wire
 *  * username=sa
 *  * password=reedbel
 *  * address=192.168.29.3:1433
 *  * databasename=mes2013_10
 *  * wifissid=TP-LINK 2.4G
 *  * wifipwd=qwe1234567
 *
 */
public class MainActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zy);
		Intent intent = new Intent(this, WifiService.class);
		intent.setClass(getApplicationContext(), XGActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
