package com.swaarm.sdkxmple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.swaarm.sdk.SwaarmAnalytics;
import com.swaarm.sdk.common.Consumer;
import com.swaarm.sdk.common.DeviceInfo;
import com.swaarm.sdk.common.model.SwaarmConfig;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        SwaarmConfig config = new SwaarmConfig(
                this.getApplication(),
                "track.hector.swaarm-app.com",
                "bff74d36-19ea-407d-bc21-bd911e068e89"
        );

        SwaarmAnalytics.configure(config, SwaarmAnalytics::enableBreakpointTracking);

        if (SwaarmAnalytics.isInitialized()) {
            findViewById(R.id.begin).setEnabled(false);
            findViewById(R.id.appSetId).setEnabled(false);
            findViewById(R.id.trackerHostname).setEnabled(false);
            findViewById(R.id.accessToken).setEnabled(false);
            return;
        }

        StartActivity activity = this;
        View beginBtn = findViewById(R.id.begin);

        EditText appSetIdInput = findViewById(R.id.appSetId);

        DeviceInfo deviceInfo = new DeviceInfo(this.getApplicationContext(), null);
        deviceInfo.setOnAppSetIdReadyListener(new Consumer<String>() {
            @Override
            public void accept(String appSetId) {
                appSetIdInput.setText(appSetId);
            }
        });

        beginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                EditText accessTokenInput = findViewById(R.id.accessToken);
                EditText trackerHostnameInput = findViewById(R.id.trackerHostname);

                String token = accessTokenInput.getText().toString();
                if (token.isEmpty()) {
                    accessTokenInput.setError("Missing access token");
                    return;
                }

                String trackerHostname = trackerHostnameInput.getText().toString();
                if (trackerHostname.isEmpty()) {
                    trackerHostnameInput.setError("Missing tracker hostname");
                    return;
                }

                String appSetId = appSetIdInput.getText().toString();
                if (appSetId.isEmpty()) {
                    appSetIdInput.setError("Missing appSetId");
                    return;
                }

                Intent eventActivityIntent = new Intent(activity, EventActivity.class);
                eventActivityIntent.putExtra("accessToken", token);
                eventActivityIntent.putExtra("trackerHostname", trackerHostname);
                eventActivityIntent.putExtra("appSetId", appSetId);

                arg0.setClickable(false);
                startActivity(eventActivityIntent);
                beginBtn.setClickable(true);
            }
        });
    }

}
