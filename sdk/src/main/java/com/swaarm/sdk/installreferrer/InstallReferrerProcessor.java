package com.swaarm.sdk.installreferrer;

import static com.swaarm.sdk.common.Logger.debug;
import static com.swaarm.sdk.common.Logger.error;

import android.content.Context;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.swaarm.sdk.installreferrer.model.InstallReferrerCallback;

public class InstallReferrerProcessor {

    private static final String LOG_TAG = "SW_install_referrer";

    private final InstallReferrerClient client;

    public InstallReferrerProcessor(Context context) {
        client = InstallReferrerClient.newBuilder(context).build();
    }

    public void fetchReferrerData(InstallReferrerCallback callback) {
        client.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                if (responseCode != InstallReferrerClient.InstallReferrerResponse.OK) {
                    error(LOG_TAG, String.format("Failed fetching install referrer, code %d",
                            responseCode));
                    return;
                }
                try {
                    // TODO replace with Consumer<ReferrerDetails> once min SDK version = 24
                    callback.processDetails(client.getInstallReferrer());
                    callback.run();
                    debug(LOG_TAG, String.format("Install referrer URL: %s",
                            client.getInstallReferrer().getInstallReferrer()));
                } catch (Exception e) {
                    error(LOG_TAG, "Failed processing install referrer data", e);
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {

            }
        });
    }
}
