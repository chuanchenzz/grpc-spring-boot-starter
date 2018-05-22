package net.devh.springboot.autoconfigure.grpc.client;

import java.util.List;

public class MethodConfig {
    private List<Name> nameList;
    private RetryPolicy retryPolicy;
    private HedgingPolicy hedgingPolicy;

    public List<Name> getNameList() {
        return nameList;
    }

    public void setNameList(List<Name> nameList) {
        this.nameList = nameList;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public HedgingPolicy getHedgingPolicy() {
        return hedgingPolicy;
    }

    public void setHedgingPolicy(HedgingPolicy hedgingPolicy) {
        this.hedgingPolicy = hedgingPolicy;
    }
}
