package com.swaarm.sdk.common;

import android.content.Context;
import android.os.Build;

import com.google.android.gms.appset.AppSet;
import com.google.android.gms.appset.AppSetIdClient;
import com.google.android.gms.appset.AppSetIdInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.swaarm.sdk.common.model.SwaarmConfig;


public class DeviceInfo {

    private  AppSetIdInfo appSetIdInfo;
    private boolean initialized = false;

    public DeviceInfo(SwaarmConfig config) {

        Context context = config.getActivity().getApplicationContext();
        AppSetIdClient client = AppSet.getClient(context);

        client.getAppSetIdInfo().addOnSuccessListener(new OnSuccessListener<AppSetIdInfo>() {
            @Override
            public void onSuccess(AppSetIdInfo info) {
                appSetIdInfo = info;
                initialized = true;
            }
        });
    }

    public String getAppSetId() {
        return appSetIdInfo != null ? appSetIdInfo.getId() : null;
    }

    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public boolean isInitialized() {
        return initialized;
    }

}
