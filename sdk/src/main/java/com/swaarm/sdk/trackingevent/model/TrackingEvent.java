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
    private final String advertisingId;
    private final String currency;
    private final PurchaseValidation purchaseValidation;

    public TrackingEvent(
            String typeId,
            Double aggregatedValue,
            String customValue,
            String vendorId,
            String clientTime,
            String osv,
            Double revenue,
            String advertisingId,
            String currency,
            PurchaseValidation purchaseValidation
    ) {
        this.purchaseValidation = purchaseValidation;
        this.id = UUID.randomUUID().toString();
        this.typeId = typeId;
        this.aggregatedValue = aggregatedValue;
        this.customValue = customValue;
        this.vendorId = vendorId;
        this.clientTime = clientTime;
        this.osv = osv;
        this.revenue = revenue;
        this.currency = currency;
        this.advertisingId = advertisingId;
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

    public String getAdvertisingId() {
        return advertisingId;
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
        obj.put("advertisingId", advertisingId);
        obj.put("currency", currency);
        if (purchaseValidation != null) {
            obj.put("androidPurchaseValidation", purchaseValidation.toJson());
        }
        return obj;
    }
}
