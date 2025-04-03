package com.swaarm.sdk.common.model;

import org.json.JSONObject;

public class AttributionData {
    private AttributionOffer offer;
    private AttributionPublisher publisher;
    private Ids ids;
    private PostbackDecision decision;

    public AttributionData() {
    }

    public static AttributionData fromJson(String jsonString) {
        AttributionData attributionData = new AttributionData();

        if (jsonString == null || jsonString.trim().isEmpty()) {
            return attributionData;
        }

        try {
            JSONObject json = new JSONObject(jsonString);

            // Parse AttributionOffer
            if (json.has("offer") && !json.isNull("offer")) {
                attributionData.offer = AttributionOffer.fromJson(json.getJSONObject("offer"));
            }

            // Parse AttributionPublisher
            if (json.has("publisher") && !json.isNull("publisher")) {
                attributionData.publisher = AttributionPublisher.fromJson(json.getJSONObject("publisher"));
            }

            // Parse Ids
            if (json.has("ids") && !json.isNull("ids")) {
                String idsJson = json.getJSONObject("ids").toString();
                attributionData.ids = Ids.fromJson(json.getJSONObject("ids"));
            }

            // Parse PostbackDecision enum
            if (json.has("decision") && !json.isNull("decision")) {
                try {
                    String decisionStr = json.getString("decision");
                    attributionData.decision = PostbackDecision.valueOf(decisionStr.toUpperCase());
                } catch (Exception e) {
                    attributionData.decision = PostbackDecision.FAILED;
                }
            }

        } catch (Exception e) {
            // Returning partially filled or empty attribution data object in case of parsing errors
            // Optionally add logging here
        }

        return attributionData;
    }


    public AttributionOffer getOffer() {
        return offer;
    }

    public void setOffer(AttributionOffer offer) {
        this.offer = offer;
    }

    public AttributionPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(AttributionPublisher publisher) {
        this.publisher = publisher;
    }

    public Ids getIds() {
        return ids;
    }

    public void setIds(Ids ids) {
        this.ids = ids;
    }

    public PostbackDecision getDecision() {
        return decision;
    }

    public void setDecision(PostbackDecision decision) {
        this.decision = decision;
    }
}
