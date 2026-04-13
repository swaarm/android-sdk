package com.swaarm.sdk.common.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SdkConfiguration {
    private static final String LOG_TAG = "SW_sdk_configuration";

    public static String PROPERTY_SWAARM_SDK_EVENT_FLUSH_FREQUENCY_IN_SECONDS = "SWAARM_SDK_EVENT_FLUSH_FREQUENCY_IN_SECONDS";
    public static String PROPERTY_SWAARM_SDK_EVENT_FLUSH_BATCH_SIZE = "SWAARM_SDK_EVENT_FLUSH_BATCH_SIZE";
    public static String PROPERTY_SWAARM_SDK_EVENT_STORAGE_SIZE_LIMIT = "SWAARM_SDK_EVENT_STORAGE_SIZE_LIMIT";

    private final Properties properties;

    public SdkConfiguration(Context context) {
        this.properties = new Properties();

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("sdk.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "An error occurred while reading sdk.properties file", e);
        }
    }

    public SdkConfiguration(Properties properties) {
        this.properties = properties;
    }

    public Long getEventFlushFrequency() {
        String value = properties.getProperty(PROPERTY_SWAARM_SDK_EVENT_FLUSH_FREQUENCY_IN_SECONDS);
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Invalid or missing " + PROPERTY_SWAARM_SDK_EVENT_FLUSH_FREQUENCY_IN_SECONDS + ", using default 10");
            return 10L;
        }
    }

    public Integer getEventFlushBatchSize() {
        String value = properties.getProperty(PROPERTY_SWAARM_SDK_EVENT_FLUSH_BATCH_SIZE);
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Invalid or missing " + PROPERTY_SWAARM_SDK_EVENT_FLUSH_BATCH_SIZE + ", using default 50");
            return 50;
        }
    }

    public Integer getEventStorageSizeLimit() {
        String value = properties.getProperty(PROPERTY_SWAARM_SDK_EVENT_STORAGE_SIZE_LIMIT);
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Invalid or missing " + PROPERTY_SWAARM_SDK_EVENT_STORAGE_SIZE_LIMIT + ", using default 500");
            return 500;
        }
    }
}
