package com.swaarm.sdk.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.gms.appset.AppSet;
import com.google.android.gms.appset.AppSetIdClient;
import com.google.android.gms.appset.AppSetIdInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.UUID;

public class DeviceInfo {

    private static final String LOG_TAG = "SW_device_info";

    private String appSetId;
    private String gaid;
    private boolean initialized = false;
    private Consumer<String> onAppSetIdReadyListener;

    public DeviceInfo(Context context, SharedPreferences settings) {
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

        client.getAppSetIdInfo().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                generateAppSetId(settings);
            }
        });

        waitForDeviceInfo();

        if (appSetId == null) {
            generateAppSetId(settings);
        }
        setGaid(context);
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

    public String getGaid() {
        return gaid;
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

    private void setGaid(Context context) {
        try {
            Object advertisingObject = Reflection.getAdvertisingInfoObject(context);
            if (advertisingObject == null) {
                return;
            }

            gaid = Reflection.getAdvertisingId(advertisingObject);
        } catch (Exception e) {
            Logger.error(LOG_TAG, "Unable to set advertising id", e);
        }
    }

    private void generateAppSetId(SharedPreferences settings) {
        if (settings != null) {
            setAppSetId(settings.getString("vendorId", UUID.randomUUID().toString()));
        }
    }
}
