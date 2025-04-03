package com.swaarm.sdk.common.model;

import com.swaarm.sdk.AttributionDataHandler;
import com.swaarm.sdk.common.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class AttributionPublisher {

    private String id;
    private String name;
    private String subId;
    private String subSubId;
    private String site;
    private String placement;
    private String creative;
    private String app;
    private String appId;
    private String unique1;
    private String unique2;
    private String unique3;
    private String groupId;

    public static AttributionPublisher fromJson(JSONObject json) {
        AttributionPublisher publisher = new AttributionPublisher();

        try {
            // Set each field if it exists in the JSON
            if (json.has("id") && !json.isNull("id")) {
                publisher.id = json.getString("id");
            }

            if (json.has("name") && !json.isNull("name")) {
                publisher.name = json.getString("name");
            }

            if (json.has("subId") && !json.isNull("subId")) {
                publisher.subId = json.getString("subId");
            }

            if (json.has("subSubId") && !json.isNull("subSubId")) {
                publisher.subSubId = json.getString("subSubId");
            }

            if (json.has("site") && !json.isNull("site")) {
                publisher.site = json.getString("site");
            }

            if (json.has("placement") && !json.isNull("placement")) {
                publisher.placement = json.getString("placement");
            }

            if (json.has("creative") && !json.isNull("creative")) {
                publisher.creative = json.getString("creative");
            }

            if (json.has("app") && !json.isNull("app")) {
                publisher.app = json.getString("app");
            }

            if (json.has("appId") && !json.isNull("appId")) {
                publisher.appId = json.getString("appId");
            }

            if (json.has("unique1") && !json.isNull("unique1")) {
                publisher.unique1 = json.getString("unique1");
            }

            if (json.has("unique2") && !json.isNull("unique2")) {
                publisher.unique2 = json.getString("unique2");
            }

            if (json.has("unique3") && !json.isNull("unique3")) {
                publisher.unique3 = json.getString("unique3");
            }

            if (json.has("groupId") && !json.isNull("groupId")) {
                publisher.groupId = json.getString("groupId");
            }

        } catch (Exception e) {
            Logger.error(AttributionDataHandler.LOG_TAG, "Could not deserialize AttributionPublisher " + json.toString(), e);
            return publisher;
        }

        return publisher;
    }

    public AttributionPublisher() {
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

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getSubSubId() {
        return subSubId;
    }

    public void setSubSubId(String subSubId) {
        this.subSubId = subSubId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getCreative() {
        return creative;
    }

    public void setCreative(String creative) {
        this.creative = creative;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUnique1() {
        return unique1;
    }

    public void setUnique1(String unique1) {
        this.unique1 = unique1;
    }

    public String getUnique2() {
        return unique2;
    }

    public void setUnique2(String unique2) {
        this.unique2 = unique2;
    }

    public String getUnique3() {
        return unique3;
    }

    public void setUnique3(String unique3) {
        this.unique3 = unique3;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributionPublisher that = (AttributionPublisher) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(subId, that.subId) && Objects.equals(subSubId, that.subSubId) && Objects.equals(site, that.site) && Objects.equals(placement, that.placement) && Objects.equals(creative, that.creative) && Objects.equals(app, that.app) && Objects.equals(appId, that.appId) && Objects.equals(unique1, that.unique1) && Objects.equals(unique2, that.unique2) && Objects.equals(unique3, that.unique3) && Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, subId, subSubId, site, placement, creative, app, appId, unique1, unique2, unique3, groupId);
    }
}
