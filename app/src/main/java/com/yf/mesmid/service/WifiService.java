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

/**
 * @author tuwei
 */
public class WifiService extends Service {
    private String mSSID = "";
    private String mBSSID = "";
    private ScanResult mScanResult = null;
    private String mKey = "";
    private boolean mResult = false;
    private boolean mIsWifiEnable = false;
    private boolean mbWifiConnect = false;
    private boolean mbWifiMutex = false;
    private WifiManager Wifimanager;
    private WifiConfiguration configuration = null;
    private String mWifiMsg = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSSID = DatabaseOper.WifiSSID;
        mKey = DatabaseOper.WifiPWD;
        Wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mWifiReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        boolean RepeatQuery = intent.getBooleanExtra("repeatquery", false);
        if (RepeatQuery) {
            mWifiMsg = DatabaseOper.RepeatWIFI_MSG;
        } else {
            mWifiMsg = DatabaseOper.FirstWIFI_MSG;
        }
        if (true == (Wifimanager.isWifiEnabled())) {
            if (isWifiConnect()) {
                WifiInfo info = Wifimanager.getConnectionInfo();
                String strSSID = info.getSSID();

                if (strSSID == null && mScanResult != null && info.getBSSID().equals(mScanResult.BSSID))
                    strSSID = mScanResult.SSID;
                if ((null != strSSID) && (strSSID.equals("\"" + mSSID + "\"") || strSSID.equals(mSSID))) {
                    mBSSID = info.getBSSID();
                    mbWifiConnect = true;
                    mbWifiMutex = true;
                    Intent WfiIntent = new Intent(mWifiMsg);
                    sendBroadcast(WfiIntent);
                    if (!RepeatQuery) WifiThread();
                    return;
                }
            }

        }

        List<WifiConfiguration> Configurations = Wifimanager.getConfiguredNetworks();
        if (null != Configurations) {
            int i = 0;
            for (; i < Configurations.size(); i++) {
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
        NetworkInfo ni = connManager.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 100: {
                    mIsWifiEnable = false;
                    mbWifiConnect = false;
                    mbWifiMutex = false;
                    configuration = null;
                    mResult = false;
                    Wifimanager.setWifiEnabled(false);
                    if (-1 != res) {
                        Wifimanager.removeNetwork(res);
                        Wifimanager.saveConfiguration();
                    }
                    Wifimanager.setWifiEnabled(true);
                    Wifimanager.startScan();
                    WifiThread();
                }
                break;
                case 101: {
                    if (isWifiConnect()) {
                        WifiInfo info = Wifimanager.getConnectionInfo();
                        if (info.getBSSID().equals(mBSSID)) {
                            mbWifiConnect = true;
                            Message message = this.obtainMessage();
                            message.arg1 = 101;
                            this.sendMessageDelayed(message, 120 * 1000);
                        } else {
                            Message message = this.obtainMessage();
                            message.arg1 = 100;
                            this.sendMessage(message);
                        }
                    } else {
                        Message message = this.obtainMessage();
                        message.arg1 = 100;
                        this.sendMessage(message);
                    }
                }
                break;
                default:
                    break;
            }
        }

        ;
    };

    public void WifiThread() {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                int degree = 0;
                while (true) {
                    try {
                        degree++;
                        Thread.sleep(1000);
						/*if(25 == degree && !mbWifiConnect){
							Message message = handler.obtainMessage();
							message.arg1 = 100;
							handler.sendMessage(message);
						}*/
                        if (35 == degree) {
                            Message message = handler.obtainMessage();
                            message.arg1 = 101;
                            handler.sendMessage(message);
                            //mIsWifiThread = false;
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    int res = -1;
    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION
                    .equals(intent.getAction())) {
            } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (null == configuration) {
                    List<ScanResult> wifiList;
                    wifiList = Wifimanager.getScanResults();
                    if (wifiList == null) {
                        return;
                    }
                    for (int i = 0; i < wifiList.size(); ) {
                        ScanResult result = wifiList.get(i++);
                        if (!mSSID.equals("") && mResult == false) {
                            mResult = mSSID.equals(result.SSID);
                            if (mResult) {
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
                                    configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);//NONE
                                    res = Wifimanager.addNetwork(configuration);
                                    Wifimanager.saveConfiguration();
                                } else {
                                    configuration.SSID = "\"" + mSSID + "\"";
                                    configuration.preSharedKey = "\"" + mKey + "\"";
                                    configuration.hiddenSSID = true;
                                    configuration.status = WifiConfiguration.Status.ENABLED;
                                    configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                                    configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                                    configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                                    configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                                    configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                                    configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                                    configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                                    res = Wifimanager.addNetwork(configuration);
                                    Wifimanager.saveConfiguration();
                                }
                                break;
                            }
                        }
                    }
                }
                if (configuration != null) {
                    if (!mIsWifiEnable) {
                        mIsWifiEnable = Wifimanager.enableNetwork(res, true);
                    }
                }
            }

            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                if (isWifiConnect() && !mbWifiMutex) {
                    WifiInfo info = Wifimanager.getConnectionInfo();
                    if (info.getBSSID().equals(mScanResult.BSSID)) {
                        mbWifiConnect = true;
                        mbWifiMutex = true;
                        //Intent WfiIntent = new Intent(c, JobListActivity.class);
                        //WfiIntent.addFlags(Intent.flag)
                        //WfiIntent.putExtra("wifi", true);
                        //startActivity(WfiIntent);
                        Intent WfiIntent = new Intent(mWifiMsg);
                        sendBroadcast(WfiIntent);
                    } else mbWifiConnect = false;
                }

                if (mbWifiConnect && configuration != null) {
                    StringBuffer buffer = new StringBuffer();
                    WifiInfo info = Wifimanager.getConnectionInfo();
                    String Myssid = info.getSSID();
                    int Speed = info.getRssi();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiReceiver);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
