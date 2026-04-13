# Swaarm Android SDK

Swaarm Android SDK is an attribution and event tracking SDK that allows you to interact with the Swaarm platform through a simple API.

## Installation

The SDK is hosted on [JitPack](https://jitpack.io/#swaarm/android-sdk). Add the JitPack repository and the SDK dependency to your project:

**Add JitPack to your root `build.gradle`:**

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}
```

**Add the SDK and required dependencies to your app's `build.gradle`:**

```groovy
dependencies {
    implementation 'com.github.swaarm:android-sdk:X.X.X' // use the latest version from JitPack
    implementation 'com.google.android.gms:play-services-appset:16.0.2'
    // Optional: only needed if you want to collect the Google Advertising ID
    implementation 'com.google.android.gms:play-services-ads-identifier:18.0.1'
}
```

### Permissions

The SDK declares the following permissions in its manifest (merged automatically):

- `android.permission.INTERNET` -- sending event data to Swaarm servers
- `android.permission.ACCESS_NETWORK_STATE` -- checking network connectivity before sending events
- `com.google.android.gms.permission.AD_ID` -- reading the Google Advertising ID (GAID)

## Configuration

Initialize the SDK as early as possible in your app, typically in your `Application.onCreate()` or launch `Activity`. Pass the current context, your Swaarm tracking domain, and your app access token:

```java
SwaarmConfig config = new SwaarmConfig(this, "https://example.swaarm.com", "<access-token>");
SwaarmAnalytics.configure(config);
```

If you need to perform actions after the SDK has finished initializing, use the callback variant:

```java
SwaarmConfig config = new SwaarmConfig(this, "https://example.swaarm.com", "<access-token>");
SwaarmAnalytics.configure(config, () -> {
    // SDK is fully initialized
    Log.d("MyApp", "Swaarm SDK ready");
});
```

### Debug Logging

Enable debug logging during development to see SDK activity in Logcat:

```java
SwaarmAnalytics.debug(true);
```

## Automatic Event Tracking

The SDK automatically tracks the following events without any additional code:

- **Install Event** -- fires once on the very first app launch after installation. Includes Google Play Install Referrer data (referrer URL, click timestamp, install begin timestamp).
- **App Open Event** (`__open`) -- fires every time the app is launched.

Both events are enriched with device metadata such as OS version, App Set ID, and (if available) the Google Advertising ID.

## Custom Events

### Sending a Custom Event

Use the `event` method to track user actions. All parameters except `typeId` are optional:

```java
// Simple event
SwaarmAnalytics.event("registration");

// Event with an aggregated value
SwaarmAnalytics.event("level_complete", 5.0);

// Event with aggregated value and custom data
SwaarmAnalytics.event("level_complete", 5.0, "{\"level\": \"castle\", \"score\": 12500}");

// Event with aggregated value, custom data, and revenue
SwaarmAnalytics.event("reward_claimed", 1.0, "daily_bonus", 0.99);
```

**Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `typeId` | `String` | The event type identifier (e.g. `"registration"`, `"level_complete"`) |
| `aggregatedValue` | `Double` | A numerical value that Swaarm aggregates in reports |
| `customValue` | `String` | A free-form string included as-is in reports |
| `revenue` | `Double` | Revenue amount associated with the event |

### Tracking Purchases

Use the `purchase` method to track in-app purchases. The SDK sends receipt data to Swaarm for server-side validation:

```java
SwaarmAnalytics.purchase(
    "subscription",       // typeId
    11.99,                // amount (revenue)
    "USD",                // currency
    "com.example.weekly", // subscriptionId or productId
    "purchase-token",     // Google Play payment token
    "premium_weekly"      // customValue
);
```

**Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `typeId` | `String` | The purchase event type (e.g. `"subscription"`, `"iap"`) |
| `amount` | `Double` | The purchase amount |
| `currency` | `String` | ISO 4217 currency code (e.g. `"USD"`, `"EUR"`) |
| `subscriptionId` | `String` | The Google Play product or subscription ID |
| `token` | `String` | The Google Play purchase token for server-side validation |
| `customValue` | `String` | A free-form string for additional purchase context |

## Attribution Data

The SDK periodically contacts the Swaarm server to retrieve attribution data using exponential backoff until valid attribution is received.

### Attribution Callback

Register a callback to be notified when attribution data becomes available:

```java
SwaarmAnalytics.onAttribution(attributionData -> {
    AttributionOffer offer = attributionData.getOffer();
    if (offer != null) {
        Log.d("MyApp", "Attributed to campaign: " + offer.getCampaignName());
    }

    Ids ids = attributionData.getIds();
    if (ids != null) {
        Log.d("MyApp", "Install ID: " + ids.getInstallId());
    }

    PostbackDecision decision = attributionData.getDecision();
    Log.d("MyApp", "Attribution decision: " + decision); // PASSED or FAILED

    GoogleInstallReferrerData referrer = attributionData.getGoogleInstallReferrer();
    if (referrer != null) {
        Log.d("MyApp", "gclid: " + referrer.getGclid());
    }
});
```

If attribution data has already been fetched before the callback is registered, it will be invoked immediately.

### Attribution Data Model

```
+-----------------------------------------------------+
|   AttributionData                                    |
+-----------------------------------------------------+
| - offer: AttributionOffer                            |
| - publisher: AttributionPublisher                    |
| - ids: Ids                                           |
| - decision: PostbackDecision                         |
| - googleInstallReferrer: GoogleInstallReferrerData   |
+-----------------------------------------------------+
        |
        |--> +-------------------------+
             |   AttributionOffer      |
             +-------------------------+
             | - id: String            |
             | - name: String          |
             | - lpId: String          |
             | - campaignId: String    |
             | - campaignName: String  |
             | - adGroupId: String     |
             | - adGroupName: String   |
             | - adId: String          |
             | - adName: String        |
             +-------------------------+
        |
        |--> +-----------------------+
             |  AttributionPublisher |
             +-----------------------+
             | - id: String          |
             | - name: String        |
             | - subId: String       |
             | - subSubId: String    |
             | - site: String        |
             | - placement: String   |
             | - creative: String    |
             | - app: String         |
             | - appId: String       |
             | - unique1: String     |
             | - unique2: String     |
             | - unique3: String     |
             | - groupId: String     |
             +-----------------------+
        |
        |--> +----------------------+
             |       Ids            |
             +----------------------+
             | - installId: String  |
             | - clickId: String    |
             | - userId: String     |
             +----------------------+
        |
        |--> +-----------------------+
             |   PostbackDecision    |
             +-----------------------+
             | - PASSED              |
             | - FAILED              |
             +-----------------------+
        |
        |--> +----------------------------------+
             | GoogleInstallReferrerData        |
             +----------------------------------+
             | - gclid: String                  |
             | - gbraid: String                 |
             | - gadSource: String              |
             | - wbraid: String                 |
             +----------------------------------+
```

#### AttributionData

Contains the full attribution response from the Swaarm server.

- **`offer`** (`AttributionOffer`) -- information about the offer involved in the attribution event.
- **`publisher`** (`AttributionPublisher`) -- information about the publisher that served the ad.
- **`ids`** (`Ids`) -- identifiers relevant to the attribution process.
- **`decision`** (`PostbackDecision`) -- the outcome of the postback process, either `PASSED` or `FAILED`.
- **`googleInstallReferrer`** (`GoogleInstallReferrerData`) -- Google Play Install Referrer identifiers.

#### AttributionOffer

Details about the offer involved in the attribution.

- **`id`** -- unique offer identifier.
- **`name`** -- offer display name.
- **`lpId`** -- landing page ID.
- **`campaignId`** / **`campaignName`** -- campaign identifier and name.
- **`adGroupId`** / **`adGroupName`** -- ad group identifier and name.
- **`adId`** / **`adName`** -- ad identifier and name.

#### AttributionPublisher

Details about the publisher responsible for serving the offer.

- **`id`** / **`name`** -- publisher identifier and name.
- **`subId`** / **`subSubId`** -- sub-identifiers for multi-level tracking.
- **`site`** -- the website or platform where the ad was displayed.
- **`placement`** -- ad placement location.
- **`creative`** -- the creative asset used.
- **`app`** / **`appId`** -- the application and its identifier.
- **`unique1`** / **`unique2`** / **`unique3`** -- custom tracking identifiers.
- **`groupId`** -- external publisher ID as defined by a third-party system (e.g. CRM, anti-fraud tool).

#### Ids

Identifiers related to the attribution process.

- **`installId`** -- identifier for the app installation.
- **`clickId`** -- identifier for the click that triggered attribution.
- **`userId`** -- identifier for the user.

#### PostbackDecision

Enum representing the attribution outcome: `PASSED` or `FAILED`.

#### GoogleInstallReferrerData

Data from Google's Install Referrer API.

- **`gclid`** -- Google Click Identifier for ad attribution.
- **`gbraid`** -- identifier for Google App Campaigns attribution.
- **`gadSource`** -- source information from Google Ads.
- **`wbraid`** -- identifier for Web-to-App attribution.

## Deferred Deep Links

The SDK supports deferred deep linking, allowing your app to respond to deep links that were clicked before the app was installed. On first launch, if a deferred deep link is available, the SDK will invoke your registered callback:

```java
SwaarmAnalytics.onDeepLink(deepLink -> {
    Log.d("MyApp", "Received deep link: " + deepLink);
    // Navigate to the appropriate screen
    Intent intent = new Intent(this, TargetActivity.class);
    intent.putExtra("route", deepLink);
    startActivity(intent);
});
```

If the deep link was already fetched before the callback is registered, it will be invoked immediately.

## Tracking Control

### Disable / Enable Tracking

You can pause and resume tracking at runtime. While disabled, no events are stored or sent:

```java
// Disable tracking (e.g. user opted out)
SwaarmAnalytics.disable();

// Re-enable tracking
SwaarmAnalytics.enable();
```

### Check Initialization Status

```java
if (SwaarmAnalytics.isInitialized()) {
    // SDK is ready to receive events
}
```

## Breakpoint Tracking

Breakpoint tracking captures activity and fragment snapshots and sends them to the Swaarm backend for visual event configuration. This is a development/setup tool -- enable it temporarily while navigating through your app to collect all possible breakpoints:

```java
SwaarmAnalytics.configure(config, () -> {
    SwaarmAnalytics.enableBreakpointTracking();
});

// Disable when done
SwaarmAnalytics.disableBreakpointTracking();
```

## SDK Configuration File

The SDK reads default settings from an `sdk.properties` file placed in your app's `assets` directory. If the file is missing, sensible defaults are used.

**`assets/sdk.properties`:**

```properties
SWAARM_SDK_EVENT_FLUSH_FREQUENCY_IN_SECONDS=10
SWAARM_SDK_EVENT_FLUSH_BATCH_SIZE=50
SWAARM_SDK_EVENT_STORAGE_SIZE_LIMIT=500
```

| Property | Default | Description |
|---|---|---|
| `SWAARM_SDK_EVENT_FLUSH_FREQUENCY_IN_SECONDS` | `10` | How often (in seconds) batched events are sent to the server |
| `SWAARM_SDK_EVENT_FLUSH_BATCH_SIZE` | `50` | Maximum number of events per batch |
| `SWAARM_SDK_EVENT_STORAGE_SIZE_LIMIT` | `500` | Maximum number of events stored locally before oldest are evicted |

## Custom App Set ID

If you need to override the automatically collected App Set ID:

```java
SwaarmAnalytics.setAppSetId("custom-id");
```

## Full Integration Example

```java
import com.swaarm.sdk.SwaarmAnalytics;
import com.swaarm.sdk.common.model.SwaarmConfig;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable debug logging during development
        SwaarmAnalytics.debug(true);

        // Configure the SDK
        SwaarmConfig config = new SwaarmConfig(
            this,
            "https://example.swaarm.com",
            "<access-token>"
        );

        SwaarmAnalytics.configure(config, () -> {
            // Register attribution callback
            SwaarmAnalytics.onAttribution(attributionData -> {
                if (attributionData.getOffer() != null) {
                    Log.d("MyApp", "Campaign: " + attributionData.getOffer().getCampaignName());
                }
            });

            // Register deep link callback
            SwaarmAnalytics.onDeepLink(deepLink -> {
                Log.d("MyApp", "Deep link: " + deepLink);
            });
        });
    }
}
```

Then track events from any Activity or service:

```java
// Track a custom event
SwaarmAnalytics.event("tutorial_complete", 1.0, "step_5");

// Track a purchase
SwaarmAnalytics.purchase(
    "iap",
    4.99,
    "USD",
    "com.example.gems_pack",
    "GPA.1234-5678-9012",
    "gems_100"
);
```

## Requirements

- **Minimum SDK**: API 14 (Android 4.0)
- **Target SDK**: API 35
- **Dependencies**: Google Play Services App Set (`play-services-appset`)
