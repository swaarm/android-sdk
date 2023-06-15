package com.swaarm.sdk.breakpoint.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SdkBreakpoint {

    private final SdkBreakpointType type;
    private final SdkBreakpointData data;

    public SdkBreakpoint(SdkBreakpointType type, SdkBreakpointData data) {
        this.type = type;
        this.data = data;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("type", type.name());
        obj.put("data", data.toJson());
        return obj;
    }
}
