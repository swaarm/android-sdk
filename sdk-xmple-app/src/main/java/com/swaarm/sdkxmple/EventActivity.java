package com.swaarm.sdkxmple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.swaarm.sdk.SwaarmAnalytics;
import com.swaarm.sdk.common.Consumer;
import com.swaarm.sdk.common.Network;
import com.swaarm.sdk.common.model.SwaarmConfig;

public class EventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        SwaarmAnalytics.debug(true);

        Bundle userConfigValues = getIntent().getExtras();

        initSdk(userConfigValues);
        handleDeviceIp();
        ((TextView) findViewById(R.id.userAgent)).setText(Network.getUserAgent(this));
        handleSendEvent();

        findViewById(com.swaarm.sdkxmple.R.id.stop).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                SwaarmAnalytics.disable();
            }
        });

        findViewById(com.swaarm.sdkxmple.R.id.resume).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                SwaarmAnalytics.enable();
            }
        });

        Intent secondaryActivityIntent = new Intent(this, SecondaryActivity.class);
        findViewById(R.id.secondaryActivity).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startActivity(secondaryActivityIntent);
            }
        });
    }

    private void handleSendEvent() {
        findViewById(R.id.event).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                EditText eventTypeInput = findViewById(R.id.eventType);
                EditText aggregatedEventValueInput = findViewById(R.id.aggregatedEventValue);
                EditText customEventValueInput = findViewById(R.id.customEvenValue);
                EditText revenueInput = findViewById(R.id.revenue);

                SwaarmAnalytics.event(
                        eventTypeInput.getText().toString(),
                        Double.valueOf(aggregatedEventValueInput.getText().toString()),
                        customEventValueInput.getText().toString(),
                        Double.valueOf(revenueInput.getText().toString())
                );
            }
        });
    }

    private void initSdk(Bundle userConfigValues) {
        SwaarmConfig config = new SwaarmConfig(
                this,
                userConfigValues.getString("trackerHostname"),
                userConfigValues.getString("accessToken")
        );

        String appSetId = userConfigValues.getString("appSetId");
        TextView appSetTextView = findViewById(R.id.appSetIdText);
        if (SwaarmAnalytics.isInitialized()) {
            SwaarmAnalytics.setAppSetId(appSetId);
            appSetTextView.setText(appSetId);
        } else {
            SwaarmAnalytics.configure(config, new Runnable() {
                @Override
                public void run() {
                    SwaarmAnalytics.enableBreakpointTracking();
                    SwaarmAnalytics.setAppSetId(appSetId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appSetTextView.setText(appSetId);
                        }
                    });

                }
            });
        }
    }

    private void handleDeviceIp() {
        TextView deviceIpView = findViewById(R.id.deviceIp);
        Utils.getExternalIpAddress(new Consumer<String>() {
            @Override
            public void accept(String ip) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deviceIpView.setText(ip);
                    }
                });
            }
        });
    }

}


