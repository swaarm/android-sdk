package com.swaarm.sdk.common.model;

import com.swaarm.sdk.AttributionDataHandler;
import com.swaarm.sdk.common.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class Ids {
    private String installId;
    private String clickId;
    private String userId;

    public Ids() {
    }

    public static Ids fromJson(JSONObject json) {
        Ids ids = new Ids();

        try {
            // Set each field if it exists in the JSON
            if (json.has("installId") && !json.isNull("installId")) {
                ids.installId = json.getString("installId");
            }

            if (json.has("clickId") && !json.isNull("clickId")) {
                ids.clickId = json.getString("clickId");
            }

            if (json.has("userId") && !json.isNull("userId")) {
                ids.userId = json.getString("userId");
            }

        } catch (Exception e) {
            Logger.error(AttributionDataHandler.LOG_TAG, "Could not deserialize Ids " + json.toString(), e);
            return ids;
        }

        return ids;
    }

    public String getInstallId() {
        return installId;
    }

    public void setInstallId(String installId) {
        this.installId = installId;
    }

    public String getClickId() {
        return clickId;
    }

    public void setClickId(String clickId) {
        this.clickId = clickId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
