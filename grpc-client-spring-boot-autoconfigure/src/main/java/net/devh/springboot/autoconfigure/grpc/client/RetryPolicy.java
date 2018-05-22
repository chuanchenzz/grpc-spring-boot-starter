package net.devh.springboot.autoconfigure.grpc.client;

import java.util.List;

public class RetryPolicy {
    private int maxAttempts;
    private String initialBackoff;
    private String maxBackoff;
    private int backoffMultiplier;
    private List<String> retryableStatusCodes;

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getInitialBackoff() {
        return initialBackoff;
    }

    public void setInitialBackoff(String initialBackoff) {
        this.initialBackoff = initialBackoff;
    }

    public String getMaxBackoff() {
        return maxBackoff;
    }

    public void setMaxBackoff(String maxBackoff) {
        this.maxBackoff = maxBackoff;
    }

    public int getBackoffMultiplier() {
        return backoffMultiplier;
    }

    public void setBackoffMultiplier(int backoffMultiplier) {
        this.backoffMultiplier = backoffMultiplier;
    }

    public List<String> getRetryableStatusCodes() {
        return retryableStatusCodes;
    }

    public void setRetryableStatusCodes(List<String> retryableStatusCodes) {
        this.retryableStatusCodes = retryableStatusCodes;
    }
}
