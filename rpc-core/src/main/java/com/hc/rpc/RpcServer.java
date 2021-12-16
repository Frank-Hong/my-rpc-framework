package com.hc.rpc;

import com.hc.rpc.serializer.CommonSerializer;

public interface RpcServer {
    void start();

    <T> void publishService(Object service,String serviceName);

    void  setSerializer(CommonSerializer serializer);
}
