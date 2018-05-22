package net.devh.springboot.autoconfigure.grpc.client;

import com.google.common.collect.Maps;
import io.grpc.Attributes;
import io.grpc.NameResolverProvider;
import io.grpc.internal.GrpcAttributes;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseNameResolverProvider extends NameResolverProvider{
    private static final String SERVICE_CONFIG_METHOD_CONFIG_KEY = "methodConfig";
    private static final String METHOD_CONFIG_RETRY_POLICY_KEY = "retryPolicy";
    private static final String RETRY_POLICY_MAX_ATTEMPTS_KEY = "maxAttempts";
    private static final String RETRY_POLICY_INITIAL_BACKOFF_KEY = "initialBackoff";
    private static final String RETRY_POLICY_MAX_BACKOFF_KEY = "maxBackoff";
    private static final String RETRY_POLICY_BACKOFF_MULTIPLIER_KEY = "backoffMultiplier";
    private static final String RETRY_POLICY_RETRYABLE_STATUS_CODES_KEY = "retryableStatusCodes";
    private static final String RETRY_POLICY_THROTTLING = "retryThrottling";
    private static final String METHOD_CONFIG_HEDGING_POLICY_KEY = "hedgingPolicy";
    private static final String HEDGING_POLICY_HEDGING_DELAY = "hedgingDelay";
    private static final String HEDGING_POLICY_NON_FATAL_STATUS_CODES = "nonFatalStatusCodes";
    private static final String MAX_TOKENS = "maxTokens";
    private static final String TOKEN_RATIO = "tokenRatio";
    private static final String SERVICE = "service";
    private static final String METHOD = "method";
    private static final String METHOD_CONFIG_NAME_KEY = "name";
    protected final ServerPropertiesResolver serverPropertiesResolver = new ServerPropertiesResolver();

    interface PropertiesResolver<T, ProT extends Properties> {
        T resolve(ProT properties);
    }

    interface Properties extends Serializable{

    }
    class ServerPropertiesResolver implements PropertiesResolver<Attributes,ServerConfigProperties>{
        @Override
        public Attributes resolve(ServerConfigProperties properties) {
            if (properties == null) {
                return Attributes.EMPTY;
            } else {
                Attributes.Builder builder = Attributes.newBuilder();
                Map<String, Object> serviceConfig = Maps.newHashMap();
                RetryThrottling retryThrottling = properties.getRetryThrottling();
                if(retryThrottling != null){
                    Map<String, Object> retryThrottlingParams = Maps.newHashMap();
                    retryThrottlingParams.put(MAX_TOKENS, retryThrottling.getMaxTokens());
                    retryThrottlingParams.put(TOKEN_RATIO, retryThrottling.getTokenRatio());
                    serviceConfig.put(RETRY_POLICY_THROTTLING,retryThrottlingParams);
                }
                List<Map<String,Object>> methodConfigList = new ArrayList<Map<String,Object>>(properties.getMethodConfigList().size());
                for (MethodConfig methodConfig : properties.getMethodConfigList()) {
                    if(!CollectionUtils.isEmpty(methodConfig.getNameList())){
                        List<Map<String,Object>> nameList = new ArrayList<Map<String,Object>>(methodConfig.getNameList().size());
                        for (Name name : methodConfig.getNameList()){
                            String serviceName = name.getService();
                            if(StringUtils.isEmpty(serviceName)){
                                continue;
                            }
                            Map<String,Object> nameMap = Maps.newHashMap();
                            nameMap.put(SERVICE,serviceName);
                            String methodName = name.getMethod();
                            if(!StringUtils.isEmpty(methodName)){
                                nameMap.put(METHOD,methodName);
                            }
                            nameList.add(nameMap);
                        }
                        if(nameList.size() > 0){
                            Map<String, Object> methodConfigMap = Maps.newHashMap();
                            methodConfigMap.put(METHOD_CONFIG_NAME_KEY,nameList);
                            RetryPolicy retryPolicy = methodConfig.getRetryPolicy();
                            HedgingPolicy hedgingPolicy = methodConfig.getHedgingPolicy();
                            if (retryPolicy != null || hedgingPolicy != null) {
                                if (retryPolicy != null) {
                                    Map<String, Object> retryPolicyParams = Maps.newHashMap();
                                    retryPolicyParams.put(RETRY_POLICY_MAX_ATTEMPTS_KEY, retryPolicy.getMaxAttempts());
                                    retryPolicyParams.put(RETRY_POLICY_INITIAL_BACKOFF_KEY, retryPolicy.getInitialBackoff());
                                    retryPolicyParams.put(RETRY_POLICY_MAX_BACKOFF_KEY, retryPolicy.getMaxBackoff());
                                    retryPolicyParams.put(RETRY_POLICY_BACKOFF_MULTIPLIER_KEY, retryPolicy.getBackoffMultiplier());
                                    retryPolicyParams.put(RETRY_POLICY_RETRYABLE_STATUS_CODES_KEY, retryPolicy.getRetryableStatusCodes());
                                    methodConfigMap.put(METHOD_CONFIG_RETRY_POLICY_KEY, retryPolicyParams);
                                } else if (hedgingPolicy != null) {
                                    Map<String, Object> hedgingPolicyParams = Maps.newHashMap();
                                    hedgingPolicyParams.put(RETRY_POLICY_MAX_ATTEMPTS_KEY, hedgingPolicy.getMaxAttempts());
                                    hedgingPolicyParams.put(HEDGING_POLICY_HEDGING_DELAY, hedgingPolicy.getHedgingDelay());
                                    hedgingPolicyParams.put(HEDGING_POLICY_NON_FATAL_STATUS_CODES, hedgingPolicy.getNonFatalStatusCodes());
                                    methodConfigMap.put(METHOD_CONFIG_HEDGING_POLICY_KEY, hedgingPolicy);
                                }
                            }
                            methodConfigList.add(methodConfigMap);
                        }else {
                            continue;
                        }
                    }else {
                        continue;
                    }
                }
                serviceConfig.put(SERVICE_CONFIG_METHOD_CONFIG_KEY, methodConfigList);
                builder.set(GrpcAttributes.NAME_RESOLVER_SERVICE_CONFIG,serviceConfig);
                return builder.build();
            }
        }
    }
}
