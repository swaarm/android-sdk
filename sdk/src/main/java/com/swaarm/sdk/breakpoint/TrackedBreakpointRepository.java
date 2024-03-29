package com.swaarm.sdk.breakpoint;

import static com.swaarm.sdk.common.Logger.*;

import android.util.Log;

import com.swaarm.sdk.breakpoint.model.SdkTrackedViewBreakpoint;
import com.swaarm.sdk.breakpoint.model.SdkTrackedBreakpoints;
import com.swaarm.sdk.common.HttpClient;
import com.swaarm.sdk.common.Logger;
import com.swaarm.sdk.common.Network;
import com.swaarm.sdk.common.model.SwaarmConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TrackedBreakpointRepository {

    private static final String LOG_TAG = "SW_breakpoint_repo";
    private final static String TRACKED_BREAKPOINTS = "/sdk-tracked-breakpoints";

    private final HttpClient httpClient;
    private final SwaarmConfig config;

    /**
     * key - view name for which to fire tracking event
     */
    private final Map<String, SdkTrackedViewBreakpoint> breakpoints = new HashMap<>();

    private boolean initialized = false;

    public TrackedBreakpointRepository(HttpClient httpClient, SwaarmConfig config) {
        this.httpClient = httpClient;
        this.config = config;
    }

    private Map<String, SdkTrackedViewBreakpoint> fetchConfiguredBreakpoints() {
        if (initialized || !Network.isNetworkAvailable(config.getContext())) {
            return breakpoints;
        }

        try {
            HttpClient.HttpResponse response = httpClient.get(config.getEventIngressHostname() + TRACKED_BREAKPOINTS);
            if (!response.isSuccess()) {
                return breakpoints;
            }

            String jsonData = response.getData();
            SdkTrackedBreakpoints trackedBreakpoints = SdkTrackedBreakpoints.fromJson(new JSONObject(jsonData));

            for (SdkTrackedViewBreakpoint breakpoint : trackedBreakpoints.getViewBreakpoints()) {
                breakpoints.put(breakpoint.getViewName(), breakpoint);
            }

            debug(LOG_TAG, String.format("'%s' tracking breakpoints read", breakpoints.size()));

            initialized = true;

        } catch (IOException | JSONException e) {
            error(LOG_TAG, "Failed to read tracked breakpoints", e);
        }

        return breakpoints;
    }

    public boolean hasBreakpoint(String viewName) {
        return fetchConfiguredBreakpoints().containsKey(viewName);
    }

    public SdkTrackedViewBreakpoint getBreakpoint(String viewName) {
        return fetchConfiguredBreakpoints().get(viewName);
    }

}
