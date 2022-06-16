package com.swaarm.sdk.common;

import android.util.Log;
import android.webkit.WebView;

import com.swaarm.sdk.common.model.SwaarmConfig;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

public class HttpClient {

    private static final String LOG_TAG = "SW_http_client";
    private final SwaarmConfig config;
    private final String userAgent;

    public HttpClient(SwaarmConfig config, String userAgent) {
        this.config = config;
        this.userAgent = userAgent;
    }

    public HttpResponse post(String connectionString, String data) throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createUrlConnection(connectionString);
            OutputStream os = urlConnection.getOutputStream();
            os.write(gzip(data));
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
            StringBuilder sb = new StringBuilder();

            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return new HttpResponse(sb.toString(), urlConnection.getResponseCode());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public HttpResponse get(String connectionString) throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createUrlConnection(connectionString);

            BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
            StringBuilder sb = new StringBuilder();

            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return new HttpResponse(sb.toString(), urlConnection.getResponseCode());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private byte[] gzip(String data) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = null;
        try {
            gzipOutputStream = new GZIPOutputStream(byteArrayOS);
            gzipOutputStream.write(data.getBytes());
            gzipOutputStream.flush();
            gzipOutputStream.close();

            return byteArrayOS.toByteArray();
        } catch (IOException e) {
            if (gzipOutputStream != null) {
                try {
                    gzipOutputStream.close();
                } catch (IOException ignored) {
                }
            }

            Log.e(LOG_TAG, "An error occurred while compressing request", e);
        }
        return new byte[]{};
    }

    private HttpURLConnection createUrlConnection(String connectionString) throws IOException {
        URL url = new URL(connectionString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Encoding", "gzip");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("User-Agent", userAgent);
        urlConnection.setRequestProperty("Authorization", "Bearer "+ config.getAccessToken());

        urlConnection.setDoOutput(true);
        urlConnection.connect();

        return urlConnection;
    }

    public static class HttpResponse {
        private String data;
        private Integer statusCode;

        public HttpResponse(String data, Integer statusCode) {
            this.data = data;
            this.statusCode = statusCode;
        }

        public String getData() {
            return data;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public boolean isSuccess() {
            return statusCode != null && statusCode >= 200 && statusCode <= 299;
        }
    }
}
