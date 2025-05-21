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

        findViewById(com.swaarm.sdkxmple.R.id.stop).setOnClickListener(arg0 -> SwaarmAnalytics.disable());
        findViewById(com.swaarm.sdkxmple.R.id.resume).setOnClickListener(arg0 -> SwaarmAnalytics.enable());

        Intent secondaryActivityIntent = new Intent(this, SecondaryActivity.class);
        findViewById(R.id.secondaryActivity).setOnClickListener(arg0 -> startActivity(secondaryActivityIntent));

        Intent billingActivityIntent = new Intent(this, BillingActivity.class);
        findViewById(R.id.billingActivity).setOnClickListener(arg0 -> startActivity(billingActivityIntent));
    }

    private void handleSendEvent() {
        findViewById(R.id.event).setOnClickListener(arg0 -> {
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
        });
    }

    private void initSdk(Bundle userConfigValues) {
        SwaarmConfig config = new SwaarmConfig(
                this.getApplication(),
                "track.hector.swaarm-app.com",
                "bff74d36-19ea-407d-bc21-bd911e068e89"
        );

        String appSetId = userConfigValues.getString("appSetId");
        TextView appSetTextView = findViewById(R.id.appSetIdText);
        if (SwaarmAnalytics.isInitialized()) {
            SwaarmAnalytics.setAppSetId(appSetId);
            appSetTextView.setText(appSetId);
        } else {
            SwaarmAnalytics.configure(config, () -> {
                SwaarmAnalytics.enableBreakpointTracking();
                SwaarmAnalytics.setAppSetId(appSetId);
                runOnUiThread(() -> appSetTextView.setText(appSetId));
            });
        }
    }

    private void handleDeviceIp() {
        TextView deviceIpView = findViewById(R.id.deviceIp);
        Utils.getExternalIpAddress(ip -> runOnUiThread(() -> deviceIpView.setText(ip)));
    }

}


