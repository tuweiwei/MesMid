package com.yf.mesmid.common;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author tuwei
 */
public class GetVersion {
    public static String getVersion(Activity activity) {
        try {
            PackageManager manager = activity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "";
        }
    }
}
