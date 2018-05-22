package net.devh.springboot.autoconfigure.grpc.client;

import java.util.List;

public class HedgingPolicy {
    private int maxAttempts;
    private String hedgingDelay;
    private List<String> nonFatalStatusCodes;

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getHedgingDelay() {
        return hedgingDelay;
    }

    public void setHedgingDelay(String hedgingDelay) {
        this.hedgingDelay = hedgingDelay;
    }

    public List<String> getNonFatalStatusCodes() {
        return nonFatalStatusCodes;
    }

    public void setNonFatalStatusCodes(List<String> nonFatalStatusCodes) {
        this.nonFatalStatusCodes = nonFatalStatusCodes;
    }
}
