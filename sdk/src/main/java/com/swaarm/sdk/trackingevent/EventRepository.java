package com.swaarm.sdk.trackingevent;

import static com.swaarm.sdk.common.Logger.*;

import static java.lang.String.*;

import com.swaarm.sdk.common.DateTime;
import com.swaarm.sdk.common.DeviceInfo;
import com.swaarm.sdk.common.model.TrackerState;
import com.swaarm.sdk.trackingevent.model.PurchaseValidation;
import com.swaarm.sdk.trackingevent.model.TrackingEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventRepository {

    private static final String LOG_TAG = "SW_event_repository";

    private final Map<String, TrackingEvent> eventStore;
    private final TrackerState trackerState;
    private final DeviceInfo deviceInfo;

    public EventRepository(TrackerState trackerState, DeviceInfo deviceInfo) {
        eventStore = Collections.synchronizedMap(new LinkedHashMap<String, TrackingEvent>(trackerState.getSdkConfiguration().getEventStorageSizeLimit() * 10 / 7, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, TrackingEvent> eldest) {
                return size() > trackerState.getSdkConfiguration().getEventStorageSizeLimit();
            }
        });
        this.trackerState = trackerState;
        this.deviceInfo = deviceInfo;
    }
    public void addEvent(String typeId, Double aggregatedValue, String customValue, Double revenue) {
       addEvent(typeId, aggregatedValue, customValue, revenue, null, null, null);
    }

    public void addEvent(
            String typeId,
            Double aggregatedValue,
            String customValue,
            Double revenue,
            String currency,
            String subscriptionId,
            String paymentToken
    ) {
        if (!trackerState.isTrackingEnabled()) {
            return;
        }
        TrackingEvent trackingEvent = new TrackingEvent(
                typeId,
                aggregatedValue,
                customValue,
                deviceInfo.getAppSetId(),
                DateTime.now(),
                deviceInfo.getOSVersion(),
                revenue,
                deviceInfo.getGaid(),
                currency,
                (subscriptionId != null || paymentToken != null) ? new PurchaseValidation(subscriptionId, paymentToken) : null
        );

        eventStore.put(trackingEvent.getId(), trackingEvent);
        debug(LOG_TAG, format("Stored event with type id '%s'. Events in store '%s'", trackingEvent.getTypeId(), eventStore.size()));
    }
    public List<TrackingEvent> getEvents(Integer limit) {
        List<TrackingEvent> events = new ArrayList<>();
        int counter = 0;
        for (TrackingEvent event : eventStore.values()) {
            if (counter++ < limit) {
                events.add(event);
            }
        }
        return events;
    }

    public void clearByIds(List<TrackingEvent> events) {
        for (TrackingEvent event : events) {
            eventStore.remove(event.getId());
        }
    }
}
