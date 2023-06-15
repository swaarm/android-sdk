package com.swaarm.sdk.breakpoint.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SdkBreakpointData {

    private final String name;
    private final String screenshot;

    public SdkBreakpointData(String name, String screenshot) {
        this.name = name;
        this.screenshot = screenshot;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("screenshot", screenshot);
        return obj;
    }
}
