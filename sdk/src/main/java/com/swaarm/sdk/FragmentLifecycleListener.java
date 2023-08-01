package com.swaarm.sdk;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.swaarm.sdk.breakpoint.ViewBreakpointEventHandler;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FragmentLifecycleListener extends FragmentManager.FragmentLifecycleCallbacks {
    private final ViewBreakpointEventHandler breakpointEventHandler;

    public FragmentLifecycleListener(ViewBreakpointEventHandler breakpointEventHandler) {
        this.breakpointEventHandler = breakpointEventHandler;
    }

    @Override
    public void onFragmentViewCreated (FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        String activityName = f.getActivity().getClass().getCanonicalName() + "#fragment[" + f.getClass().getCanonicalName() + "]";
        breakpointEventHandler.handle(v, activityName);
    }
}
