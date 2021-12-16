package com.hc.rpc.provider;

public interface ServiceProvider {
    <T> void register(T service);

    Object getService(String serviceName);
}
