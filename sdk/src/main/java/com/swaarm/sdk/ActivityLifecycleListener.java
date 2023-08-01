package com.swaarm.sdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.swaarm.sdk.breakpoint.ViewBreakpointEventHandler;

public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

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

        String activityName = activity.getClass().getCanonicalName();

        breakpointEventHandler.handle(
                activity.getWindow().getDecorView().getRootView(),
                activityName
        );
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            activity.getFragmentManager().registerFragmentLifecycleCallbacks(
                    new FragmentLifecycleListener(breakpointEventHandler),
                    false
            );
        }
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
