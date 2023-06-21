package com.swaarm.sdk.trackingevent.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseValidation {

    private String subscriptionId;
    private String token;

    public PurchaseValidation(String subscriptionId, String token) {
        this.subscriptionId = subscriptionId;
        this.token = token;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("subscriptionId", subscriptionId);
        obj.put("token", token);
        return obj;
    }
}
