# Swaarm Android SDK

### Permissions
  * "android.permission.ACCESS_NETWORK_STATE" - for collecting network related information (is device connected/disconnected)
  * "android.permission.INTERNET" - for sending data to our servers

### Dependencies

https://jitpack.io/#swaarm/android-sdk - package repository
  ```
  dependencies {
      implementation 'com.google.android.gms:play-services-appset:16.0.2'
      implementation 'com.github.swaarm:android-sdk:X.X.X' //pick the latest version. Same as tag name in Github
  }
  ```
### Configuration

 Create configuration object with current activity, swaarm event ingress hostname and access token:
 ```
   SwaarmConfig config = new SwaarmConfig(this, "https://organization.swaarm.com", "access-token")
   SwaarmAnalytics.configure(config)
 ```
To send event:
 ```
  SwaarmAnalytics.event("event_type_id", 123D, "custom value", 321D)
 ```

 If display details about tracking: `SwaarmAnalytics.debug(true)`

 To disable tracking runtime: `SwaarmAnalytics.disable()`

 To enable tracking runtime: `SwaarmAnalytics.enable()`



