package net.devh.springboot.autoconfigure.grpc.client;

import net.devh.springboot.autoconfigure.grpc.client.model.MethodConfig;
import net.devh.springboot.autoconfigure.grpc.client.model.RetryThrottling;

import java.util.List;

public class ServerConfigProperties implements BaseNameResolverProvider.Properties{
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
