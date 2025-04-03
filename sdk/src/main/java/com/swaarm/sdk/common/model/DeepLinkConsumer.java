package com.swaarm.sdk.common.model;

/**
 * Consumer interface for apps that wish to be notified if a deep link is available for a user
 */
public interface DeepLinkConsumer {
    void accept(String deeplink);
}
