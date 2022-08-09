package com.swaarm.sdk.common.model;

public class TrackerState {

    private final SwaarmConfig config;
    private final SdkConfiguration sdkConfiguration;
    private final Session session;
    private boolean trackingEnabled = true;
    private boolean breakpointTracking = false;

    public TrackerState(SwaarmConfig config, SdkConfiguration sdkConfiguration, Session session) {
        this.config = config;
        this.sdkConfiguration = sdkConfiguration;
        this.session = session;
    }

    public void setTrackingEnabled(boolean trackingEnabled) {
        this.trackingEnabled = trackingEnabled;
    }

    public void setBreakpointTracking(boolean breakpointTracking) {
        this.breakpointTracking = breakpointTracking;
    }

    public Session getSession() {
        return session;
    }

    public boolean isTrackingEnabled() {
        return trackingEnabled;
    }

    public boolean isBreakpointTrackingEnabled() {
        return breakpointTracking;
    }

    public SwaarmConfig getConfig() {
        return config;
    }

    public SdkConfiguration getSdkConfiguration() {
        return sdkConfiguration;
    }
}
