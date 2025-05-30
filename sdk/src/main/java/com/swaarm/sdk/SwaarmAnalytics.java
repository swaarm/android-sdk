package com.swaarm.sdk;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.swaarm.sdk.breakpoint.BreakpointAppSetIdRepository;
import com.swaarm.sdk.breakpoint.BreakpointScreenshotCapture;
import com.swaarm.sdk.breakpoint.TrackedBreakpointRepository;
import com.swaarm.sdk.breakpoint.ViewBreakpointEventHandler;
import com.swaarm.sdk.common.DeviceInfo;
import com.swaarm.sdk.common.HttpClient;
import com.swaarm.sdk.common.Logger;
import com.swaarm.sdk.common.Network;
import com.swaarm.sdk.common.model.AttributionDataConsumer;
import com.swaarm.sdk.common.model.DeepLinkConsumer;
import com.swaarm.sdk.common.model.SdkConfiguration;
import com.swaarm.sdk.common.model.Session;
import com.swaarm.sdk.common.model.SwaarmConfig;
import com.swaarm.sdk.common.model.TrackerState;
import com.swaarm.sdk.installreferrer.InstallReferrerProcessor;
import com.swaarm.sdk.installreferrer.model.InstallReferrerCallback;
import com.swaarm.sdk.installreferrer.model.InstallReferrerData;
import com.swaarm.sdk.trackingevent.EventPublisher;
import com.swaarm.sdk.trackingevent.EventRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SwaarmAnalytics {

    private static final String LOG_TAG = "SW_api";
    private static final AtomicBoolean starting = new AtomicBoolean(false);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static Boolean initialized = false;
    private static TrackerState trackerState;
    private static EventRepository eventRepository;
    private static DeviceInfo deviceInfo;
    private static InstallReferrerProcessor installReferrerProcessor;
    private static AttributionDataHandler attributionDataHandler;

    public static void configure(final SwaarmConfig config) {
        configure(config, null);
    }

    public void onAttribution(AttributionDataConsumer consumer) {
        attributionDataHandler.setAttributionDataConsumer(consumer);
    }

    public void onDeepLink(DeepLinkConsumer deepLinkConsumer) {
        attributionDataHandler.setDeepLinkConsumer(deepLinkConsumer);
    }

    public static void configure(final SwaarmConfig config, Runnable onComplete) {
        if (initialized) {
            Logger.debug(LOG_TAG, "Already initialized");
            return;
        }

        List<String> validationMessages = config.validate();
        if (!validationMessages.isEmpty()) {
            for (String message : validationMessages) {
                Logger.debug(LOG_TAG, message);
            }
            return;
        }

        String ua = Network.getUserAgent(config.getContext());

        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (!starting.compareAndSet(false, true)) {
                    Logger.debug(LOG_TAG, "Already started initialization");
                    return;
                }

                Context applicationContext = config.getContext().getApplicationContext();
                SharedPreferences settings = applicationContext.getSharedPreferences("SWAARM_SDK", Context.MODE_PRIVATE);
                try {
                    SdkConfiguration sdkConfiguration = new SdkConfiguration(applicationContext);

                    trackerState = new TrackerState(config, sdkConfiguration, new Session());
                    HttpClient httpClient = new HttpClient(config, ua);
                    deviceInfo = new DeviceInfo(applicationContext, settings);

                    eventRepository = new EventRepository(trackerState, deviceInfo);

                    installReferrerProcessor = new InstallReferrerProcessor(applicationContext);

                    BreakpointAppSetIdRepository breakpointAppSetIdRepository = new BreakpointAppSetIdRepository(httpClient, config);

                    BreakpointScreenshotCapture screenshotCapture = new BreakpointScreenshotCapture(
                            deviceInfo,
                            breakpointAppSetIdRepository
                    );

                    TrackedBreakpointRepository breakpointRepository = new TrackedBreakpointRepository(httpClient, config);
                    ViewBreakpointEventHandler viewBreakpointHandler = new ViewBreakpointEventHandler(
                            executor,
                            breakpointRepository,
                            screenshotCapture,
                            eventRepository,
                            httpClient,
                            trackerState
                    );

                    if (config.getContext() instanceof Application) {
                        Application app = ((Application) config.getContext());
                        app.registerActivityLifecycleCallbacks(new ActivityLifecycleListener(viewBreakpointHandler));
                    }

                    EventPublisher eventPublisher = new EventPublisher(eventRepository, trackerState, httpClient);
                    eventPublisher.start();

                    attributionDataHandler = new AttributionDataHandler(httpClient, config, deviceInfo.getAppSetId());
                    attributionDataHandler.startAttribution();

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to initialize Swaarm SDK", e);
                    return;
                }

                initialized = true;
                sendInitialEvents(settings);

                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }


    public static void installEvent(InstallReferrerData installReferrerData) {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                eventRepository.addInstallEvent(installReferrerData);
            }
        });
    }

    public static void event(String typeId) {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                eventRepository.addEvent(typeId, 0.0D, null, 0.0D);
            }
        });
    }

    public static void event(String typeId, Double aggregatedValue) {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                eventRepository.addEvent(typeId, aggregatedValue, null, 0.0D);
            }
        });
    }

    public static void event(String typeId, Double aggregatedValue, String customValue) {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                eventRepository.addEvent(typeId, aggregatedValue, customValue, 0.0D);
            }
        });
    }

    public static void event(String typeId, Double aggregatedValue, String customValue, Double revenue) {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                eventRepository.addEvent(typeId, aggregatedValue, customValue, revenue);
            }
        });
    }

    public static void purchase(String typeId, Double amount, String currency, String subscriptionId, String token, String customValue) {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                eventRepository.addEvent(typeId, 1d, customValue, amount, currency, subscriptionId, token);
            }
        });
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
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                trackerState.setTrackingEnabled(false);
                Logger.debug(LOG_TAG, "Tracking disabled");
            }
        });
    }

    /**
     * Resume tracking
     */
    public static void enable() {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                trackerState.setTrackingEnabled(true);
                Logger.debug(LOG_TAG, "Tracking resumed");
            }
        });
    }

    /**
     * Disable breakpoint tracking, disables sending events according to configured sdk app breakpoints
     */
    public static void disableBreakpointTracking() {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                trackerState.setBreakpointTracking(false);
                Logger.debug(LOG_TAG, "Breakpoint tracking disabled");
            }
        });
    }

    /**
     * Set custom App set id
     *
     * @param id app set id
     */
    public static void setAppSetId(String id) {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                deviceInfo.setAppSetId(id);
            }
        });
    }

    /**
     * Returns true if sdk successfully initialized
     *
     * @return boolean
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Resume breakpoint tracking
     */
    public static void enableBreakpointTracking() {
        executeWhenInitialized(new Runnable() {
            @Override
            public void run() {
                trackerState.setBreakpointTracking(true);
                Logger.debug(LOG_TAG, "Breakpoint tracking resumed");
            }
        });
    }

    private static void sendInitialEvents(SharedPreferences settings) {
        boolean ranAlready = settings.getBoolean("ranAlready", false);
        //Very first time app open event is null
        if (!ranAlready) {
            installReferrerProcessor.fetchReferrerData(new InstallReferrerCallback() {
                @Override
                public void run() {
                    installEvent(getInstallReferrerData());
                    settings.edit().putBoolean("ranAlready", true).apply();
                }
            });

        }
        //each time app open
        event("__open");
    }

    private static void executeWhenInitialized(Runnable runnable) {
        if (!initialized) {
            Logger.debug(LOG_TAG, "SDK configuration not initialized. Please configure with 'SwaarmAnalytics.configure'");
            return;
        }
        try {
            runnable.run();
        } catch (Exception e) {
            Logger.error(LOG_TAG, "An error occurred while executing Swaarm SDK API call", e);
        }
    }

}
