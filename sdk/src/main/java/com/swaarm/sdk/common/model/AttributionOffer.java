package com.swaarm.sdk.common.model;

import com.swaarm.sdk.AttributionDataHandler;
import com.swaarm.sdk.common.Logger;

import org.json.JSONObject;

import java.util.Objects;

public class AttributionOffer {
    private String id;
    private String name;
    private String lpId;
    private String campaignId;
    private String campaignName;
    private String adGroupId;
    private String adGroupName;
    private String adId;
    private String adName;

    public AttributionOffer() {
    }

    public static AttributionOffer fromJson(JSONObject json) {
        AttributionOffer offer = new AttributionOffer();

        try {

            // Set each field if it exists in the JSON
            if (json.has("id") && !json.isNull("id")) {
                offer.id = json.getString("id");
            }

            if (json.has("name") && !json.isNull("name")) {
                offer.name = json.getString("name");
            }

            if (json.has("lpId") && !json.isNull("lpId")) {
                offer.lpId = json.getString("lpId");
            }

            if (json.has("campaignId") && !json.isNull("campaignId")) {
                offer.campaignId = json.getString("campaignId");
            }

            if (json.has("campaignName") && !json.isNull("campaignName")) {
                offer.campaignName = json.getString("campaignName");
            }

            if (json.has("adGroupId") && !json.isNull("adGroupId")) {
                offer.adGroupId = json.getString("adGroupId");
            }

            if (json.has("adGroupName") && !json.isNull("adGroupName")) {
                offer.adGroupName = json.getString("adGroupName");
            }

            if (json.has("adId") && !json.isNull("adId")) {
                offer.adId = json.getString("adId");
            }

            if (json.has("adName") && !json.isNull("adName")) {
                offer.adName = json.getString("adName");
            }

        } catch (Exception e) {
            Logger.error(AttributionDataHandler.LOG_TAG, "Could not deserialize AttributionOffer " + json.toString(), e);
            return offer;
        }

        return offer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLpId() {
        return lpId;
    }

    public void setLpId(String lpId) {
        this.lpId = lpId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getAdGroupId() {
        return adGroupId;
    }

    public void setAdGroupId(String adGroupId) {
        this.adGroupId = adGroupId;
    }

    public String getAdGroupName() {
        return adGroupName;
    }

    public void setAdGroupName(String adGroupName) {
        this.adGroupName = adGroupName;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributionOffer that = (AttributionOffer) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(lpId, that.lpId) && Objects.equals(campaignId, that.campaignId) && Objects.equals(campaignName, that.campaignName) && Objects.equals(adGroupId, that.adGroupId) && Objects.equals(adGroupName, that.adGroupName) && Objects.equals(adId, that.adId) && Objects.equals(adName, that.adName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lpId, campaignId, campaignName, adGroupId, adGroupName, adId, adName);
    }
}
