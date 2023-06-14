package com.swaarm.sdk.breakpoint;

import android.util.Log;

import com.swaarm.sdk.common.HttpClient;
import com.swaarm.sdk.common.Network;
import com.swaarm.sdk.common.model.SwaarmConfig;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * The app set ids for which collection of new breakpoints are enabled.
 *
 */
public class BreakpointAppSetIdRepository {

    private static final String LOG_TAG = "SW_breakpoint_repo";
    private final static String ALLOWED_SDK_BREAKPOINT_COLLECTORS_PATH = "/sdk-allowed-breakpoint-collectors";

    private final HttpClient httpClient;
    private final SwaarmConfig config;

    private final Set<String> ids = new HashSet<>();

    private boolean initialized = false;

    public BreakpointAppSetIdRepository(HttpClient httpClient, SwaarmConfig config) {
        this.httpClient = httpClient;
        this.config = config;
    }

    private Set<String> fetchEnabledIds() {
        if (initialized || !Network.isNetworkAvailable(config.getActivity().getApplication())) {
            return ids;
        }

        try {
            HttpClient.HttpResponse response = httpClient.get(config.getEventIngressHostname() + ALLOWED_SDK_BREAKPOINT_COLLECTORS_PATH);
            if (!response.isSuccess()) {
                return ids;
            }

            String jsonData = response.getData();
            if (jsonData.isEmpty()) {
                return ids;
            }
            JSONArray enabledIds = new JSONArray(jsonData.split(","));

            for (int i = 0; i < enabledIds.length(); i++) {
                ids.add(enabledIds.getString(i));
            }

            initialized = true;

        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, "Failed to read app set ids", e);
        }

        return ids;
    }

    public boolean hasId(String id) {
        return fetchEnabledIds().contains(id);
    }
}
