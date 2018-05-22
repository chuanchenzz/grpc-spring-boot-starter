package net.devh.springboot.autoconfigure.grpc.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GrpcChannelProperties {
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final Integer DEFAULT_PORT = 9090;

    public static final GrpcChannelProperties DEFAULT = new GrpcChannelProperties();

    private List<String> host = new ArrayList<String>() {
        private static final long serialVersionUID = -8367871342050560040L;

        {
            add(DEFAULT_HOST);
        }
    };
    private List<Integer> port = new ArrayList<Integer>() {
        private static final long serialVersionUID = 4705083089654936515L;

        {
            add(DEFAULT_PORT);
        }
    };

    private boolean plaintext = true;

    /**
     * Setting to enable keepalive.
     * Default to {@code false}
     */
    private boolean enableKeepAlive = false;

    /**
     * Sets whether keepalive will be performed when there are no outstanding RPC on a connection.
     * Defaults to {@code false}.
     */
    private boolean keepAliveWithoutCalls = false;

    /**
     * The default delay in seconds before we send a keepalive.
     * Defaults to {@code 180}
     */
    private long keepAliveTime = 180;

    /**
     * The default timeout in seconds for a keepalive ping request.
     * Defaults to {@code 20}
     */
    private long keepAliveTimeout = 20;

    /**
     * The maximum message size allowed to be received on the channel.
     */
    private int maxInboundMessageSize;

    /**
     * if open retry policy
     */
    private boolean enableRetry;

    /**
     * when open retry,setting maxRetry attempts
     */
    private int maxRetryAttempts;

    private Map<String,Object> serverConfig;

    public List<String> getHost() {
        return host;
    }

    public void setHost(List<String> host) {
        this.host = host;
    }

    public List<Integer> getPort() {
        return port;
    }

    public void setPort(List<Integer> port) {
        this.port = port;
    }

    public boolean isPlaintext() {
        return plaintext;
    }

    public void setPlaintext(boolean plaintext) {
        this.plaintext = plaintext;
    }

    public boolean isEnableKeepAlive() {
        return enableKeepAlive;
    }

    public void setEnableKeepAlive(boolean enableKeepAlive) {
        this.enableKeepAlive = enableKeepAlive;
    }

    public boolean isKeepAliveWithoutCalls() {
        return keepAliveWithoutCalls;
    }

    public void setKeepAliveWithoutCalls(boolean keepAliveWithoutCalls) {
        this.keepAliveWithoutCalls = keepAliveWithoutCalls;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public void setMaxInboundMessageSize(int maxInboundMessageSize) {
        this.maxInboundMessageSize = maxInboundMessageSize;
    }

    public boolean isEnableRetry() {
        return enableRetry;
    }

    public void setEnableRetry(boolean enableRetry) {
        this.enableRetry = enableRetry;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public Map<String, Object> getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(Map<String, Object> serverConfig) {
        this.serverConfig = serverConfig;
    }
}
