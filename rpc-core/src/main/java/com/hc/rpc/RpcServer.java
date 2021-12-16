package com.hc.rpc;

import com.hc.rpc.serializer.CommonSerializer;

public interface RpcServer {
    void start();

    <T> void publishService(Object service,Class<T> serviceClass);

    void  setSerializer(CommonSerializer serializer);
}
