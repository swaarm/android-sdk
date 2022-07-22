package com.swaarm.sdk.trackingevent.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class TrackingEvent {

    private final String id;
    private final String typeId;
    private final Double aggregatedValue;
    private final String customValue;
    private final String vendorId;
    private final String clientTime;
    private final String osv;
    private final Double revenue;

    public TrackingEvent(
            String typeId,
            Double aggregatedValue,
            String customValue,
            String vendorId,
            String clientTime,
            String osv,
            Double revenue
    ) {
        this.id = UUID.randomUUID().toString();
        this.typeId = typeId;
        this.aggregatedValue = aggregatedValue;
        this.customValue = customValue;
        this.vendorId = vendorId;
        this.clientTime = clientTime;
        this.osv = osv;
        this.revenue = revenue;
    }

    public String getId() {
        return id;
    }

    public String getTypeId() {
        return typeId;
    }

    public Double getAggregatedValue() {
        return aggregatedValue;
    }

    public String getCustomValue() {
        return customValue;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getClientTime() {
        return clientTime;
    }

    public String getOsv() {
        return osv;
    }

    public Double getRevenue() {
        return revenue;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("typeId", typeId);
        obj.put("aggregatedValue", aggregatedValue);
        obj.put("customValue", customValue);
        obj.put("vendorId", vendorId);
        obj.put("clientTime", clientTime);
        obj.put("osv", osv);
        obj.put("revenue", revenue);
        return obj;
    }
}
