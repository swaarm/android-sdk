package com.swaarm.sdk.common;

import android.content.Context;
import android.os.Build;

import com.google.android.gms.appset.AppSet;
import com.google.android.gms.appset.AppSetIdClient;
import com.google.android.gms.appset.AppSetIdInfo;
import com.google.android.gms.tasks.OnSuccessListener;

public class DeviceInfo {

    private String appSetId;
    private boolean initialized = false;
    private Consumer<String> onAppSetIdReadyListener;

    public DeviceInfo(Context context) {
        AppSetIdClient client = AppSet.getClient(context);
        client.getAppSetIdInfo().addOnSuccessListener(new OnSuccessListener<AppSetIdInfo>() {
            @Override
            public void onSuccess(AppSetIdInfo info) {
                initialized = true;
                appSetId = info.getId();
                if (onAppSetIdReadyListener != null) {
                    onAppSetIdReadyListener.accept(info.getId());
                }
            }
        });

        waitForDeviceInfo();
    }

    public void setOnAppSetIdReadyListener(Consumer<String> appSetIdReadyListener) {
        this.onAppSetIdReadyListener = appSetIdReadyListener;
    }

    public String getAppSetId() {
        return appSetId != null ? appSetId : null;
    }

    public void setAppSetId(String appSetId) {
        this.appSetId = appSetId;
    }

    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public boolean isInitialized() {
        return initialized;
    }

    private void waitForDeviceInfo() {
        int retries = 30;
        while (!isInitialized() && retries-- > 0) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
