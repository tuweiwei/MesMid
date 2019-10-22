package com.yf.mesmid.consts;

import android.os.Environment;

public interface MyConsts {
    String ConfigPath = Environment.getExternalStorageDirectory().getPath()+"/MESConfig.ini";
    int ERROR_NOEXIT = 100;
    int CONNECT_SUCCESS = 101;
    int SCANBARCODE_SUCCESS = 102;
    int GET_UPDATEINFO = 1;

    int ERRORDIALOG_CANCEL = 120;
    int UPDATE = 130;
    int UPDATE_ERROR = 131;
    int CX_SUCCESS = 103;

    String savePath = Environment.getExternalStorageDirectory().getPath() + "/updateApk/";
    String apkUrl = "http://192.168.11.100/MESMid.apk";

}
