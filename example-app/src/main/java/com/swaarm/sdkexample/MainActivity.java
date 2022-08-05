package com.swaarm.sdkexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.swaarm.sdk.SwaarmAnalytics;
import com.swaarm.sdk.common.model.SwaarmConfig;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SwaarmConfig config = new SwaarmConfig(this, "https://organization.swaarm.com", "123");
        Intent secondaryActivityIntent = new Intent(this, SecondaryActivity.class);

        SwaarmAnalytics.configure(config);
        SwaarmAnalytics.debug(true);

        findViewById(com.swaarm.sdkexample.R.id.event).setOnClickListener(new View.OnClickListener() {
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

        findViewById(com.swaarm.sdkexample.R.id.stop).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                SwaarmAnalytics.disable();
            }
        });

        findViewById(com.swaarm.sdkexample.R.id.resume).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                SwaarmAnalytics.enable();
            }
        });

        findViewById(R.id.secondaryActivity).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
               startActivity(secondaryActivityIntent);
            }
        });
    }
}


