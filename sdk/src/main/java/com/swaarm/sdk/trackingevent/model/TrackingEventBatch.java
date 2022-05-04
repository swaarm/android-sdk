package com.swaarm.sdk.trackingevent.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TrackingEventBatch {
    private final List<TrackingEvent> events;
    private final String time;

    public TrackingEventBatch(List<TrackingEvent> events, String time) {
        this.events = events;
        this.time = time;
    }

    public List<TrackingEvent> getEvents() {
        return events;
    }

    public String getTime() {
        return time;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray eventArr = new JSONArray();
        for (TrackingEvent event : events) {
            eventArr.put(event.toJson());
        }
        obj.put("events", eventArr);
        obj.put("time", time);
        return obj;
    }
}
