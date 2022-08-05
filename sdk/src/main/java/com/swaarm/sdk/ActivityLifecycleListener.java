package com.swaarm.sdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.swaarm.sdk.breakpoint.ViewBreakpointEventHandler;

public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    private static final String LOG_TAG = "SW_lifecycle_event";
    private final ViewBreakpointEventHandler breakpointEventHandler;

    public ActivityLifecycleListener(ViewBreakpointEventHandler breakpointEventHandler) {
        this.breakpointEventHandler = breakpointEventHandler;
    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {
        if (activity.getWindow() == null ||
                activity.getWindow().getDecorView() == null ||
                activity.getWindow().getDecorView().getRootView() == null) {
            return;
        }


        breakpointEventHandler.handle(
                activity.getWindow().getDecorView().getRootView(),
                activity.getClass().getCanonicalName()
        );
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
}
