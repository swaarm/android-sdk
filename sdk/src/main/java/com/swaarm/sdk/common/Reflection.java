package com.swaarm.sdk.common;

import android.content.Context;

import java.lang.reflect.Method;

public class Reflection {

    public static Object getAdvertisingInfoObject(Context context) throws Exception {
        return invokeStaticMethod("com.google.android.gms.ads.identifier.AdvertisingIdClient", "getAdvertisingIdInfo", new Class[]{Context.class}, context);
    }

    public static String getAdvertisingId(Object object) throws Exception {
        return (String) Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient$Info").getDeclaredMethod("getId").invoke(object);
    }

    public static Object invokeStaticMethod(String className, String methodName, Class[] cArgs, Object... args) throws Exception {
        Class classObject = Class.forName(className);
        return invokeMethod(classObject, methodName, null, cArgs, args);
    }

    public static Object invokeMethod(Class classObject, String methodName, Object instance, Class[] cArgs, Object... args) throws Exception {
        Method methodObject = classObject.getMethod(methodName, cArgs);
        return methodObject.invoke(instance, args);
    }

}
