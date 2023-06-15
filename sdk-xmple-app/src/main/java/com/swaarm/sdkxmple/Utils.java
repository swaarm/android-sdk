package com.swaarm.sdkxmple;

import com.swaarm.sdk.common.Consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Executors;

public class Utils {

    public static void getExternalIpAddress(Consumer<String> ipConsumer)  {
        Executors.newSingleThreadExecutor().execute(() -> {
            BufferedReader in = null;
            try {
                URL whatIsMyIp = new URL("https://checkip.amazonaws.com");
                in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()));
                String ip = in.readLine();
                ipConsumer.accept(ip);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
