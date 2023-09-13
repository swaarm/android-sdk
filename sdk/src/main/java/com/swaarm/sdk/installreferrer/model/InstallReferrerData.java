package com.swaarm.sdk.installreferrer.model;

import org.json.JSONException;
import org.json.JSONObject;

public class InstallReferrerData {
    private final long clickTimestamp;

    private final long clickServerTimestamp;

    private final long installBeginTimestamp;

    private final long installBeginServerTimestamp;

    private final String installVersion;

    private final boolean instantPlay;

    private final String referrer;

    public InstallReferrerData(long clickTimestamp, long clickServerTimestamp,
                               long installBeginTimestamp, long installBeginServerTimestamp,
                               String installVersion, boolean instantPlay, String referrer) {
        this.clickTimestamp = clickTimestamp;
        this.clickServerTimestamp = clickServerTimestamp;
        this.installBeginTimestamp = installBeginTimestamp;
        this.installBeginServerTimestamp = installBeginServerTimestamp;
        this.installVersion = installVersion;
        this.instantPlay = instantPlay;
        this.referrer = referrer;
    }
    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("clickTimestamp", clickTimestamp);
        obj.put("clickServerTimestamp", clickServerTimestamp);
        obj.put("installBeginTimestamp", installBeginTimestamp);
        obj.put("installBeginServerTimestamp", installBeginServerTimestamp);
        obj.put("installVersion", installVersion);
        obj.put("instantPlay", instantPlay);
        if (referrer != null) {
            obj.put("referrer", referrer);
        }
        return obj;
    }
}
