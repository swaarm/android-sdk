package com.swaarm.sdk.common.model;

/**
 * Consumer interface for apps that wish to be notified once the attribution was completed
 */
public interface AttributionDataConsumer {
    void accept(AttributionData data);
}
