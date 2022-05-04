package com.swaarm.sdk.common;

import android.content.Context;
import android.content.pm.PackageManager;

public class Permission {

    public static boolean isGranted(Context context, String permission) {
        return (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }
}
