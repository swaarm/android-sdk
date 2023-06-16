package com.swaarm.sdk.common.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SwaarmConfig {

    private final Context context;
    private final String eventIngressHostname;
    private final String accessToken;

    public SwaarmConfig(Context context, String eventIngressHostname, String accessToken) {
        this.context = context;

        if (!eventIngressHostname.startsWith("http")) {
            eventIngressHostname = "https://" + eventIngressHostname;
        }

        this.eventIngressHostname = eventIngressHostname.replace("/$", "");
        this.accessToken = accessToken;
    }

    public Context getContext() {
        return context;
    }

    public String getEventIngressHostname() {
        return eventIngressHostname;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public List<String> validate() {
        List<String> messages = new ArrayList<>();

        if (accessToken == null) {
            messages.add("Missing access token");
        }

        if (eventIngressHostname == null) {
            messages.add("Missing event ingress hostname");
        }

        if (context == null) {
            messages.add("Activity is not set");
        }

        return messages;
    }
}
