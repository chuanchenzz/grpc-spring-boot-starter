package net.devh.springboot.autoconfigure.grpc.client;

public class RetryThrottling {
    private int maxTokens;
    private double tokenRatio;

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public double getTokenRatio() {
        return tokenRatio;
    }

    public void setTokenRatio(double tokenRatio) {
        this.tokenRatio = tokenRatio;
    }
}
