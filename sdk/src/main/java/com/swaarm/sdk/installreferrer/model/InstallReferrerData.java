package com.swaarm.sdk.installreferrer.model;

import org.json.JSONException;
import org.json.JSONObject;

public class InstallReferrerData {
    private final long clickTimestamp;

    private final long installBeginTimestamp;

    private final String referrer;

    public InstallReferrerData(long clickTimestamp, long installBeginTimestamp, String referrer) {
        this.clickTimestamp = clickTimestamp;
        this.installBeginTimestamp = installBeginTimestamp;
        this.referrer = referrer;
    }
    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("clickTimestamp", clickTimestamp);
        obj.put("installBeginTimestamp", installBeginTimestamp);
        if (referrer != null) {
            obj.put("referrer", referrer);
        }
        return obj;
    }
}
