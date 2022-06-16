package com.swaarm.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.WebView;

import com.swaarm.sdk.common.DeviceInfo;
import com.swaarm.sdk.common.HttpClient;
import com.swaarm.sdk.common.Logger;
import com.swaarm.sdk.common.model.SdkConfiguration;
import com.swaarm.sdk.common.model.Session;
import com.swaarm.sdk.common.model.SwaarmConfig;
import com.swaarm.sdk.common.model.TrackerState;
import com.swaarm.sdk.trackingevent.EventPublisher;
import com.swaarm.sdk.trackingevent.EventRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SwaarmAnalytics {

    private static final String LOG_TAG = "SW_api";
    private static final AtomicBoolean started = new AtomicBoolean(false);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static HttpClient httpClient;
    private static Boolean initialized = false;
    private static TrackerState trackerState;
    private static EventRepository eventRepository;

    public static void configure(final SwaarmConfig config) {
        List<String> validationMessages = config.validate();
        if (!validationMessages.isEmpty()) {
            for (String message : validationMessages) {
                Logger.debug(LOG_TAG, message);
            }
            return;
        }

        String ua = new WebView(config.getActivity()).getSettings().getUserAgentString();

        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (started.compareAndSet(false, true)) {
                    Context applicationContext = config.getActivity().getApplicationContext();
                    SharedPreferences settings = applicationContext.getSharedPreferences("SWAARM_SDK", Context.MODE_PRIVATE);
                    try {
                        SdkConfiguration sdkConfiguration = new SdkConfiguration(applicationContext);

                        trackerState = new TrackerState(config, sdkConfiguration, new Session());
                        httpClient = new HttpClient(config, ua);
                        DeviceInfo deviceInfo = new DeviceInfo(config);

                        waitForInitialization(deviceInfo);

                        eventRepository = new EventRepository(trackerState, deviceInfo);
                        EventPublisher eventPublisher = new EventPublisher(eventRepository, trackerState, httpClient);
                        eventPublisher.start();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Failed to initialize Swaarm SDK", e);
                        return;
                    }

                    initialized = true;

                    boolean ranAlready = settings.getBoolean("ranAlready", false);
                    //Very first event is null
                    if (!ranAlready) {
                        event(null, 0.0D);
                        settings.edit().putBoolean("ranAlready", true).apply();
                    }
                } else {
                    Logger.debug(LOG_TAG, "Already configured");
                }
            }
        });
    }

    private static void waitForInitialization(DeviceInfo deviceInfo) {
        int retries = 30;
        while (!deviceInfo.isInitialized() && retries-- > 0) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignored) {
            }
        }
    }
    public static void event(String typeId, Double aggregatedValue) {
        if (!initialized) {
            Log.e(LOG_TAG, "SDK configuration not initialized. Please configure with 'SwaarmAnalytics.configure'");
            return;
        }
        eventRepository.addEvent(typeId, aggregatedValue, null);
    }

    public static void event(String typeId, Double aggregatedValue, String customValue) {
        if (!initialized) {
            Log.e(LOG_TAG, "SDK configuration not initialized. Please configure with 'SwaarmAnalytics.configure'");
            return;
        }
        eventRepository.addEvent(typeId, aggregatedValue, customValue);
    }

    /**
     * Enable/Disable debug logger output
     */
    public static void debug(boolean enabled) {
        Logger.setIsEnabled(enabled);
    }

    /**
     * Disable tracking, wont store any data to local storage or send events to Swaarm event API
     */
    public static void disable() {
        if (!initialized) {
            Log.e(LOG_TAG, "SDK configuration not initialized. Please configure with 'SwaarmAnalytics.configure'");
            return;
        }
        trackerState.setTrackingEnabled(false);
        Logger.debug(LOG_TAG, "Tracking disabled");
    }

    /**
     * Resume tracking
     */
    public static void enable() {
        if (!initialized) {
            Log.e(LOG_TAG, "SDK configuration not initialized. Please configure with 'SwaarmAnalytics.configure'");
            return;
        }
        trackerState.setTrackingEnabled(true);
        Logger.debug(LOG_TAG, "Tracking resumed");
    }

}
