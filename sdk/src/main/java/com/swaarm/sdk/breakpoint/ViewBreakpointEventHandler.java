package com.swaarm.sdk.breakpoint;

import static com.swaarm.sdk.common.Logger.*;

import android.util.Log;
import android.view.View;

import com.swaarm.sdk.breakpoint.model.SdkBreakpoint;
import com.swaarm.sdk.breakpoint.model.SdkBreakpointData;
import com.swaarm.sdk.breakpoint.model.SdkTrackedViewBreakpoint;
import com.swaarm.sdk.breakpoint.model.SdkBreakpointType;
import com.swaarm.sdk.common.Consumer;
import com.swaarm.sdk.common.HttpClient;
import com.swaarm.sdk.common.model.TrackerState;
import com.swaarm.sdk.trackingevent.EventRepository;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class ViewBreakpointEventHandler {

    private static final String LOG_TAG = "SW_breakpoint_handler";
    private static final String SDK_BREAKPOINTS_ENDPOINT = "/sdk-breakpoints";

    private final ExecutorService executor;
    private final TrackedBreakpointRepository breakpointEventsRepository;
    private final BreakpointScreenshotCapture breakpointScreenshotCapture;
    private final EventRepository eventRepository;
    private final HttpClient httpClient;
    private final TrackerState trackerState;

    /**
     * List of activities captured during app session. This prevents sending same activity multiple times
     *
     * Stores activity name (class simple name)
     */
    private final Set<String> sessionBreakpointCaptures = new HashSet<>();

    public ViewBreakpointEventHandler(
            ExecutorService executor,
            TrackedBreakpointRepository breakpointEventsRepository,
            BreakpointScreenshotCapture breakpointScreenshotCapture,
            EventRepository eventRepository,
            HttpClient httpClient,
            TrackerState config
    ) {
        this.executor = executor;
        this.breakpointEventsRepository = breakpointEventsRepository;
        this.breakpointScreenshotCapture = breakpointScreenshotCapture;
        this.eventRepository = eventRepository;
        this.httpClient = httpClient;
        this.trackerState = config;
    }

    public void handle(View view, String activityName) {
        if (!trackerState.isBreakpointTrackingEnabled()) {
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!breakpointEventsRepository.hasBreakpoint(activityName)) {
                        //capture same breakpoint only once per session
                        if (sessionBreakpointCaptures.contains(activityName)) {
                            return;
                        }

                        breakpointScreenshotCapture.takeScreenshot(view, new Consumer<String>() {
                            @Override
                            public void accept(String screenshotData) {
                                sendBreakpoint(screenshotData, activityName);
                            }
                        });
                        return;
                    }

                    SdkTrackedViewBreakpoint breakpoint = breakpointEventsRepository.getBreakpoint(activityName);

                    debug(LOG_TAG, String.format("Breakpoint for event '%s' and activity '%s' captured.", activityName, breakpoint.getEventType()));
                    eventRepository.addEvent(breakpoint.getEventType(), 1D, null, null);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to handle breakpoint", e);
                }
            }
        });
    }

    private void sendBreakpoint(String imageData, String activityName) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                SdkBreakpoint sdkBreakpoint = new SdkBreakpoint(
                        SdkBreakpointType.VIEW,
                        new SdkBreakpointData(activityName, imageData)
                );

                try {
                    HttpClient.HttpResponse response = httpClient.post(trackerState.getConfig().getEventIngressHostname() + SDK_BREAKPOINTS_ENDPOINT, sdkBreakpoint.toJson().toString());
                    if (response.isSuccess()) {
                        debug(LOG_TAG, String.format("Breakpoint for activity '%s' saved", activityName));
                        sessionBreakpointCaptures.add(activityName);
                    } else {
                        debug(LOG_TAG, String.format("Failed to save breakpoint for activity '%s'", activityName));
                    }
                } catch (IOException | JSONException e) {
                    Log.e(LOG_TAG, String.format("Failed to handle breakpoint for activity %s", activityName), e);
                }
            }
        });
    }

}
