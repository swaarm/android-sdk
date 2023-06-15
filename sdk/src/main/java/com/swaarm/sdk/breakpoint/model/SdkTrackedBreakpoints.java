package com.swaarm.sdk.breakpoint.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SdkTrackedBreakpoints {

    private final List<SdkTrackedViewBreakpoint> viewBreakpoints;

    public SdkTrackedBreakpoints(List<SdkTrackedViewBreakpoint> viewBreakpoints) {
        this.viewBreakpoints = viewBreakpoints;
    }

    public List<SdkTrackedViewBreakpoint> getViewBreakpoints() {
        return viewBreakpoints;
    }

    public static SdkTrackedBreakpoints fromJson(JSONObject jsonObject) throws JSONException {
        return new SdkTrackedBreakpoints(getViewBreakpoints(jsonObject));
    }

    private static List<SdkTrackedViewBreakpoint> getViewBreakpoints(JSONObject jsonObject) throws JSONException {
        List<SdkTrackedViewBreakpoint> viewBreakpoints = new ArrayList<>();
        JSONArray breakpoints = jsonObject.getJSONArray("viewBreakpoints");
        for (int i = 0; i < breakpoints.length(); i++) {
            viewBreakpoints.add(SdkTrackedViewBreakpoint.fromJson(breakpoints.getJSONObject(i)));
        }
        return viewBreakpoints;
    }
}
