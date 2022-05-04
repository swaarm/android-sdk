package com.swaarm.sdk.trackingevent;

import static com.swaarm.sdk.common.Logger.*;
import static java.lang.String.*;

import android.util.Log;

import com.swaarm.sdk.common.DateTime;
import com.swaarm.sdk.common.HttpClient;
import com.swaarm.sdk.common.Network;
import com.swaarm.sdk.common.model.TrackerState;
import com.swaarm.sdk.trackingevent.model.TrackingEvent;
import com.swaarm.sdk.trackingevent.model.TrackingEventBatch;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventPublisher {

    private static final String LOG_TAG = "SW_event_publisher";
    private static final Integer LIMIT_OF_FAILED_ATTEMPTS = 30;
    private static final Long DELAYED_EVENT_PUBLISHER_IN_SECONDS = 10L;
    private static final String EVENT_PATH = "/sdk";

    private int failedAttempts = 0;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final EventRepository eventRepository;
    private final TrackerState trackerState;
    private final HttpClient httpClient;

    public EventPublisher(
            EventRepository eventRepository,
            TrackerState trackerState,
            HttpClient httpClient
    ) {
        this.eventRepository = eventRepository;
        this.trackerState = trackerState;
        this.httpClient = httpClient;
    }

    public void start() {
        failedAttempts = 0;
        debug(LOG_TAG, "Started");

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (shouldStop()) {
                        debug(LOG_TAG, "Stopping publisher. Too many failed attempts");
                        scheduler.shutdown();
                        return;
                    }

                    if (shouldSkip()) {
                        return;
                    }

                    List<TrackingEvent> events = eventRepository.getEvents(
                        trackerState.getSdkConfiguration().getEventFlushBatchSize()
                    );

                    if (events.isEmpty()) {
                        return;
                    }

                    sendEvents(events);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "An error occurred while publishing tracking event", e);
                    failedAttempts++;
                }
            }
        }, DELAYED_EVENT_PUBLISHER_IN_SECONDS, trackerState.getSdkConfiguration().getEventFlushFrequency(), TimeUnit.SECONDS);
    }

    private boolean shouldStop() {
        return failedAttempts >= LIMIT_OF_FAILED_ATTEMPTS;
    }

    private boolean shouldSkip() {
        if (!trackerState.isTrackingEnabled()) {
            return true;
        }
        return !isConnected();
    }

    public boolean isConnected() {
        return Network.isNetworkAvailable(trackerState.getConfig().getActivity().getApplication());
    }

    private void sendEvents(final List<TrackingEvent> events) throws JSONException, IOException {
        debug(LOG_TAG, format(Locale.getDefault(), "Sending event batch request with '%d' events. ", events.size()));

        TrackingEventBatch eventBatch = new TrackingEventBatch(events, DateTime.now());
        HttpClient.HttpResponse response = httpClient.post(
                trackerState.getConfig().getEventIngressHostname() + EVENT_PATH,
               eventBatch.toJson().toString()
        );

        if (response.isSuccess()) {
            eventRepository.clearByIds(events);
            debug(LOG_TAG, format(Locale.getDefault(), "Event batch request successfully sent. Received '%d' status code", response.getStatusCode()));
            return;
        }

        failedAttempts++;
        debug(LOG_TAG, format(Locale.getDefault(), "Failed to sent event batch request. Received '%d' status code", response.getStatusCode()));
    }
}
