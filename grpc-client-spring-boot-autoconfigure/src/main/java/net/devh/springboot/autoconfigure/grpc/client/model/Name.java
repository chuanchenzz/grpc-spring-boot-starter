package net.devh.springboot.autoconfigure.grpc.client.model;

public class Name {
    private String service;
    private String method;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
