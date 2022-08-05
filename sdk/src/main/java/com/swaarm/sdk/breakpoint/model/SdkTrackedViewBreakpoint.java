package com.swaarm.sdk.breakpoint.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SdkTrackedViewBreakpoint {

    private final String viewName;
    private final String eventType;

    public SdkTrackedViewBreakpoint(String viewName, String eventType) {
        this.viewName = viewName;
        this.eventType = eventType;
    }

    public String getViewName() {
        return viewName;
    }

    public String getEventType() {
        return eventType;
    }

    public static SdkTrackedViewBreakpoint fromJson(JSONObject jsonObject) throws JSONException {
        return new SdkTrackedViewBreakpoint(
                jsonObject.getString("viewName"),
                jsonObject.getString("eventType")
        );
    }

}
