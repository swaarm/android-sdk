package com.swaarm.sdk.common.model;

import com.swaarm.sdk.AttributionDataHandler;
import com.swaarm.sdk.common.Logger;

import org.json.JSONObject;

public class GoogleInstallReferrerData {
    private String gclid;
    private String gbraid;
    private String gadSource;
    private String wbraid;

    public GoogleInstallReferrerData() {
    }

    public static GoogleInstallReferrerData fromJson(JSONObject json) {
        GoogleInstallReferrerData data = new GoogleInstallReferrerData();

        try {
            if (json.has("gclid") && !json.isNull("gclid")) {
                data.gclid = json.getString("gclid");
            }

            if (json.has("gbraid") && !json.isNull("gbraid")) {
                data.gbraid = json.getString("gbraid");
            }

            if (json.has("gadSource") && !json.isNull("gadSource")) {
                data.gadSource = json.getString("gadSource");
            }

            if (json.has("wbraid") && !json.isNull("wbraid")) {
                data.wbraid = json.getString("wbraid");
            }

        } catch (Exception e) {
            Logger.error(AttributionDataHandler.LOG_TAG, "Could not deserialize GoogleInstallReferrerData " + json.toString(), e);
            return data;
        }

        return data;
    }

    public String getGclid() {
        return gclid;
    }

    public void setGclid(String gclid) {
        this.gclid = gclid;
    }

    public String getGbraid() {
        return gbraid;
    }

    public void setGbraid(String gbraid) {
        this.gbraid = gbraid;
    }

    public String getGadSource() {
        return gadSource;
    }

    public void setGadSource(String gadSource) {
        this.gadSource = gadSource;
    }

    public String getWbraid() {
        return wbraid;
    }

    public void setWbraid(String wbraid) {
        this.wbraid = wbraid;
    }
}
