# Swaarm Android SDK

### Permissions

* "android.permission.ACCESS_NETWORK_STATE" - for collecting network related information (is device
  connected/disconnected)
* "android.permission.INTERNET" - for sending data to our servers
* "com.google.android.gms.permission.AD_ID" - for reading advertising ID (Gaid)

### Dependencies

https://jitpack.io/#swaarm/android-sdk - package repository

  ```
  dependencies {
      implementation 'com.google.android.gms:play-services-appset:16.0.2'
      implementation 'com.github.swaarm:android-sdk:X.X.X' //pick the latest version. Same as tag name in Github
  }
  ```

### Configuration

Create configuration object with current activity, swaarm event tracking api hostname and App access
token:

 ```
   SwaarmConfig config = new SwaarmConfig(this, "https://organization.swaarm.com", "access-token")
   SwaarmAnalytics.configure(config)
   
   SwaarmAnalytics.configure(config, () -> {
         //called when configuration initialization completed
         //Check the logs for errors when unable to initialize
    });
 ```

Sending event:

 ```
  SwaarmAnalytics.event("event_type_id", 123D, "custom value", 321D)
 ```

To display SDK debug information: `SwaarmAnalytics.debug(true)`

To disable tracking runtime: `SwaarmAnalytics.disable()`

To enable tracking runtime: `SwaarmAnalytics.enable()`

To activate breakpoint tracking: `SwaarmAnalytics.enableBreakpointTracking()`. This will collect your App activity breakpoints and send to our backend for further event configuration. 
Navigate through all the app while this active to have all the possible breakpoints collected.





