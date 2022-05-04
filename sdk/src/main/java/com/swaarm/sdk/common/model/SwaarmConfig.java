package com.swaarm.sdk.common.model;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;

public class SwaarmConfig {

    private Activity activity;
    private String eventIngressHostname;
    private String accessToken;

    public SwaarmConfig(Activity activity, String eventIngressHostname, String accessToken) {
        this.activity = activity;
        this.eventIngressHostname = eventIngressHostname;
        this.accessToken = accessToken;
    }

    public Activity getActivity() {
        return activity;
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
        if (activity == null) {
            messages.add("Activity is not set");
        }

        return messages;
    }
}
