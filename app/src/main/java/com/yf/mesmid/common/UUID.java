package com.yf.mesmid.common;

import android.app.Activity;
import android.provider.Settings;
import android.text.TextUtils;

public final class UUID {
    public String getMyUUID(Activity activity){
        String UUID = Settings.System.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        String ret=(!TextUtils.isEmpty(UUID) ? UUID : null);
        return ret;
    }
}
