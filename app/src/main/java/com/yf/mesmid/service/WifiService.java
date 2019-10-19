package com.yf.mesmid.service;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.yf.mesmid.db.DatabaseOper;

public class WifiService extends Service
{
	private final String TAG = "MESWifiService";
	
    private String mSSID = "";
    private String mBSSID = "";
    private ScanResult mScanResult = null;
    private String mKey = "";
    private boolean mResult = false;
    private boolean mIsWifiEnable = false;
    private boolean mIsWifiThread = false;
    private boolean mbWifiConnect = false;
    private boolean mbWifiMutex = false;
    private boolean mbWifiThread = true;
    private WifiManager Wifimanager;
	private WifiConfiguration configuration = null;
	private String mWifiMsg = null;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "WifiService onCreate");
		mSSID = DatabaseOper.WifiSSID;
		if("".equals(mSSID)) {
			Toast.makeText(this, "mSSIDΪ�գ����������ļ�", 2000).show();
			stopSelf();
		}
		mKey = DatabaseOper.WifiPWD;
		Wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(mWifiReceiver, filter);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG, "WifiService onStart");
		boolean RepeatQuery = intent.getBooleanExtra("repeatquery", false);	
		if(RepeatQuery){
			mWifiMsg = DatabaseOper.RepeatWIFI_MSG;
		}else{
			mWifiMsg = DatabaseOper.FirstWIFI_MSG;
		}
		if(true == (Wifimanager.isWifiEnabled())){
			if(isWifiConnect()){
				WifiInfo info = Wifimanager.getConnectionInfo();
				String strSSID = info.getSSID();
				/*String strSSID = "Wi-Fi ����";
				if(null == strSSID){
					while(null ==mScanResult){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};*/
				/*��strSSIDΪ�գ���鿴��ǰ�Ƿ���ɨ�����*/
				if(strSSID == null && mScanResult != null && info.getBSSID().equals(mScanResult.BSSID)) 
					strSSID = mScanResult.SSID;
				//else strSSID = "";
				if( (null != strSSID) && (strSSID.equals("\""+mSSID+"\"")
						|| strSSID.equals(mSSID))) {
					mBSSID = info.getBSSID();
					mbWifiConnect = true;
					mbWifiMutex = true;
					Intent WfiIntent = new Intent(mWifiMsg);
					sendBroadcast(WfiIntent);
					if( ! RepeatQuery )WifiThread();
					return;
				}
			}
			
		}
		if(RepeatQuery){
			mIsWifiEnable = false;
		    mIsWifiThread = false;
		    mbWifiConnect = false;
		    mbWifiMutex = false;
		    mbWifiThread = false;
		}
		List<WifiConfiguration> Configurations = Wifimanager.getConfiguredNetworks();
		if(null != Configurations){
			int i = 0;
			for(; i < Configurations.size(); i++)
			{
				WifiConfiguration Wificonfiguration = Configurations.get(i);
				Wifimanager.removeNetwork(Wificonfiguration.networkId);
			}
			Wifimanager.saveConfiguration();
		}
		configuration = null;
		mResult = false;
		Wifimanager.setWifiEnabled(false);
		Wifimanager.setWifiEnabled(true);
		Wifimanager.startScan();
		WifiThread();
	}
	
	public boolean isWifiConnect() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		//NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		//return mWifi.isConnected();
		NetworkInfo ni = connManager.getActiveNetworkInfo();
		return (ni != null && ni.isConnectedOrConnecting());
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.arg1) {
			case 100:
				{
					Log.d(TAG, "--------------35s-----------");
					mIsWifiEnable = false;
					mbWifiConnect = false;
					mbWifiMutex = false;
					mIsWifiThread = false;
				    mbWifiThread = false;
					configuration = null;
					mResult = false;
					Wifimanager.setWifiEnabled(false);
					if(-1 != res){
						Wifimanager.removeNetwork(res);
						Wifimanager.saveConfiguration();
					}
					Wifimanager.setWifiEnabled(true);
					Wifimanager.startScan();
				    WifiThread();
				}
				break;
			case 101:
			    {
			    	if(isWifiConnect()){
						WifiInfo info = Wifimanager.getConnectionInfo();
						//if(info.getSSID().equals("\"" +mSSID+"\"")
							//	|| info.getSSID().equals(mSSID)) {
						if(info.getBSSID().equals(mBSSID)){
							mbWifiConnect = true;
							Message message = this.obtainMessage();
							message.arg1 = 101;
							this.sendMessageDelayed(message, 120 * 1000);
						}else {
							Message message = this.obtainMessage();
							message.arg1 = 100;
							this.sendMessage(message);
						}
					}else{
						Message message = this.obtainMessage();
						message.arg1 = 100;
						this.sendMessage(message);
					}
			    	
			    }
				break;
			default:
				break;
			}
		};
	};
	
	public void WifiThread() {

		 new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				int degree = 0;
				Log.d(TAG, "WifiThread Start");
				while (true) {
					//if(!mbWifiThread) break;
					try {
						degree++;
						Log.d("---", ":" + degree);
						Thread.sleep(1000);
						/*if(25 == degree && !mbWifiConnect){
							Message message = handler.obtainMessage();
							message.arg1 = 100;
							handler.sendMessage(message);
						}*/
						if(35 == degree){
							Message message = handler.obtainMessage();
							message.arg1 = 101;
							handler.sendMessage(message);
							//mIsWifiThread = false;
							break;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
				Log.d(TAG, "WifiThread Stop");
			}
		}.start();
	}
	
	int res = -1;
	private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

		public void onReceive(Context c, Intent intent) {
			if (WifiManager.WIFI_STATE_CHANGED_ACTION
					.equals(intent.getAction())) {

			} else if (intent.getAction().equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				//Toast.makeText(WifiService.this, "SCAN_RESULTS_AVAILABLE_ACTION",2000).show();
				Log.d(TAG, " WifiReceiver: SCAN_RESULTS_AVAILABLE_ACTION");
				if(null == configuration){
					List<ScanResult> wifiList;
					wifiList = Wifimanager.getScanResults();
					if (wifiList == null)
						return;
				//	dbhandler.postDelayed(dbrunnable, 0);
					for (int i = 0; i < wifiList.size();) {
						ScanResult result = (ScanResult) wifiList.get(i++);

						if (!mSSID.equals("")  && mResult == false) {
							mResult = mSSID.equals(result.SSID);
	                        if(mResult){
	                        	mBSSID = result.BSSID;
	                        	mScanResult = result;
	                        	configuration = new WifiConfiguration();
	    						if (mKey.equals("")) {
	    							configuration.allowedAuthAlgorithms.clear();
	    							configuration.allowedGroupCiphers.clear();
	    							configuration.allowedKeyManagement.clear();
	    							configuration.allowedPairwiseCiphers.clear();
	    							configuration.allowedProtocols.clear();
	    							configuration.SSID = "\"" + mSSID + "\"";
	    							//configuration.wepKeys[0] = "";//����Ҫ��
	    							configuration.allowedKeyManagement
	    									.set(WifiConfiguration.KeyMgmt.NONE);//NONE
	    							//configuration.wepTxKeyIndex = 0;
	    							//configuration.status = WifiConfiguration.Status.ENABLED;
	    							res = Wifimanager.addNetwork(configuration);
	    							Wifimanager.saveConfiguration();

	    						} else {
	    							configuration.SSID = "\"" + mSSID + "\"";
	    							configuration.preSharedKey = "\"" + mKey + "\""; // ���ȵ������
	    							configuration.hiddenSSID = true;
	    							configuration.status = WifiConfiguration.Status.ENABLED;
	    							configuration.allowedAuthAlgorithms
	    									.set(WifiConfiguration.AuthAlgorithm.OPEN);
	    							configuration.allowedGroupCiphers
	    									.set(WifiConfiguration.GroupCipher.TKIP);
	    							configuration.allowedGroupCiphers
	    									.set(WifiConfiguration.GroupCipher.CCMP);
	    							configuration.allowedKeyManagement
	    									.set(WifiConfiguration.KeyMgmt.WPA_PSK);
	    							configuration.allowedPairwiseCiphers
	    									.set(WifiConfiguration.PairwiseCipher.TKIP);
	    							configuration.allowedPairwiseCiphers
	    									.set(WifiConfiguration.PairwiseCipher.CCMP);
	    							configuration.allowedProtocols
	    									.set(WifiConfiguration.Protocol.WPA);
	    							res = Wifimanager.addNetwork(configuration);
	    							Wifimanager.saveConfiguration();
	    						    //Toast.makeText(Selftest.this, "SCAN_RESULTS_AVAILABLE_ACTION"+i,2000).show();
	    						}
	    						//level = result.level;
	    						break;
	                        }
						}
					}
				}
				if (configuration != null) {
					// config���ȿ�
					if (!mIsWifiEnable) {
						//mIsWifiEnable = mWifiManager.enableNetwork(0, true);
						mIsWifiEnable = Wifimanager.enableNetwork(res, true);
						//Toast.makeText(Selftest.this, "----config���ȿ�"+res,3000).show();
						Log.e(TAG, "-----mIsWifiEnable = true;---");
					}
				}
				/*if (mIsWifiThread == false) {
					Log.d(TAG, "--------dddddd");
					mbWifiThread = true;
					WifiThread();
					mIsWifiThread = true;
				}*/
			}
				if (intent.getAction().equals(
						WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
					    //Toast.makeText(Selftest.this, "NETWORK_STATE_CHANGED_ACTION",2000).show();
						Log.d(TAG, "NETWORK_STATE_CHANGED_ACTION--true---");
						if(isWifiConnect() && ! mbWifiMutex){
							WifiInfo info = Wifimanager.getConnectionInfo();
							//if(info.getSSID().equals("\"" +mSSID+"\"")
									//|| info.getSSID().equals(mSSID)) {
							Log.i(TAG, "BSSID: "+info.getBSSID()+"   "+mScanResult.BSSID);
							if(info.getBSSID().equals(mScanResult.BSSID)){
								mbWifiConnect = true;
								mbWifiMutex = true;
								//Intent WfiIntent = new Intent(c, JobListActivity.class);
								//WfiIntent.addFlags(Intent.flag)
								//WfiIntent.putExtra("wifi", true);
								//startActivity(WfiIntent);
								Intent WfiIntent = new Intent(mWifiMsg);
								sendBroadcast(WfiIntent);
							}
							else mbWifiConnect = false;
						}
						
						//mbWifiConnect = true;
					if (mbWifiConnect && configuration != null) {
						StringBuffer buffer = new StringBuffer();
						WifiInfo info = Wifimanager.getConnectionInfo();
						String Myssid = info.getSSID();
						int Speed = info.getRssi();
					}
				}
				else{
					Log.d(TAG, "else WifiReceiver: "+intent.getAction());

				}
			}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mWifiReceiver);
		mbWifiThread = false;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Toast.makeText(this, "WifiService onDestroy", 2000).show();
		Log.d(TAG, "WifiService onDestroy");
	}
}
