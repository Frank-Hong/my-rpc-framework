package com.hc.rpc.provider;

public interface ServiceProvider {
    <T> void register(T service,String serviceName);

    Object getService(String serviceName);
}
