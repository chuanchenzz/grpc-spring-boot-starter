package net.devh.springboot.autoconfigure.grpc.client;

import java.util.List;

public class ServerConfigProperties {
    private List<MethodConfig> methodConfigList;
    private RetryThrottling retryThrottling;

    public List<MethodConfig> getMethodConfigList() {
        return methodConfigList;
    }

    public void setMethodConfigList(List<MethodConfig> methodConfigList) {
        this.methodConfigList = methodConfigList;
    }

    public RetryThrottling getRetryThrottling() {
        return retryThrottling;
    }

    public void setRetryThrottling(RetryThrottling retryThrottling) {
        this.retryThrottling = retryThrottling;
    }
}
