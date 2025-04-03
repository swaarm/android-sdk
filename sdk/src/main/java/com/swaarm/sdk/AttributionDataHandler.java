package com.swaarm.sdk;

import com.swaarm.sdk.common.HttpClient;
import com.swaarm.sdk.common.Logger;
import com.swaarm.sdk.common.model.AttributionData;
import com.swaarm.sdk.common.model.AttributionDataConsumer;
import com.swaarm.sdk.common.model.DeepLinkConsumer;
import com.swaarm.sdk.common.model.SwaarmConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AttributionDataHandler {
    public static final String LOG_TAG = "SW_attribution_data";
    private final String attributionDataUrl;
    private final String deepLinkUrl;
    private final String userId;
    private final HttpClient httpClient;
    private AttributionData attributionData = null;
    private String deepLink = null;
    private AttributionDataConsumer attributionDataConsumer;
    private DeepLinkConsumer deepLinkConsumer;
    private final int delayBase = 2;
    private AtomicInteger exponent = new AtomicInteger(1);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AttributionDataHandler(HttpClient httpClient, SwaarmConfig config, String userId) {
        this.attributionDataUrl = config.getEventIngressHostname() + "/attribution-data";
        this.deepLinkUrl = config.getEventIngressHostname() + "/deeplink";

        this.httpClient = httpClient;
        this.userId = userId;
    }

    public void setAttributionDataConsumer(AttributionDataConsumer attributionDataConsumer) {
        this.attributionDataConsumer = attributionDataConsumer;
        if (attributionData != null) {
            attributionDataConsumer.accept(attributionData);
        }
    }

    public void setDeepLinkConsumer(DeepLinkConsumer deepLinkConsumer) {
        this.deepLinkConsumer = deepLinkConsumer;
        if (deepLink != null) {
            deepLinkConsumer.accept(deepLink);
        }
    }

    public void startAttribution() {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                boolean result = fetch();
                if (result) {
                    fetchDeepLink();
                }
                if (!result) {
                    if (exponent.incrementAndGet() > 1024) {
                        return;
                    }
                    startAttribution();
                }

            }
        }, (long) Math.pow(delayBase, exponent.get()), TimeUnit.SECONDS);
    }

    private boolean fetch() {
        String url = this.attributionDataUrl + "&vendorId=" + userId;
        try {
            HttpClient.HttpResponse response = httpClient.get(url);
            if (response.isSuccess()) {
                attributionData = AttributionData.fromJson(response.getData());
                if (attributionData.getDecision() != null) {
                    Logger.debug(LOG_TAG, "Received attribution data: " + attributionData);
                    attributionDataConsumer.accept(attributionData);
                    return true;
                }
            }
            Logger.error(LOG_TAG, "Could not read attribution data response for user " + userId + "; Response: " + response.toString());
        } catch (Exception e) {
            Logger.error(LOG_TAG, "Could not read attribution data response for user " + userId, e);
        }
        return false;
    }

    private boolean fetchDeepLink() {
        try {
            HttpClient.HttpResponse response = httpClient.get(this.deepLinkUrl);
            if (response.isSuccess()) {
                if (attributionData != null && attributionData.getDecision() != null) {
                    deepLinkConsumer.accept(response.getData());
                    this.deepLink = response.getData();
                    return true;
                }
            }
            Logger.error(LOG_TAG, "Could not read deep link data response for user " + userId + "; Response: " + response.toString());
        } catch (Exception e) {
            Logger.error(LOG_TAG, "Could not read deep link data response for user " + userId, e);
        }
        return false;
    }
}
