package com.swaarm.sdk.installreferrer.model;

import com.android.installreferrer.api.ReferrerDetails;

public abstract class InstallReferrerCallback implements Runnable {
    private InstallReferrerData installReferrerData;

    public void processDetails(ReferrerDetails referrerDetails) {
        this.installReferrerData = new InstallReferrerData(
                referrerDetails.getReferrerClickTimestampSeconds(),
                referrerDetails.getInstallBeginTimestampSeconds(),
                referrerDetails.getInstallReferrer());
    }

    public InstallReferrerData getInstallReferrerData() {
        return installReferrerData;
    }

    @Override
    public abstract void run();
}
