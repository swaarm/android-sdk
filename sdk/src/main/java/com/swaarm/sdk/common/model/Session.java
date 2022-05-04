package com.swaarm.sdk.common.model;

import java.util.UUID;

public class Session {

    private Long start;
    private String sessionId;
    private String userId;

    public Session() {
        this.start = System.currentTimeMillis();
        this.sessionId = UUID.randomUUID().toString();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public Long getStart() {
        return start;
    }

    public String getSessionId() {
        return sessionId;
    }
}
