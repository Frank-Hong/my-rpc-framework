package com.hc.rpc;

import com.hc.rpc.entity.RpcRequest;
import com.hc.rpc.serializer.CommonSerializer;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);

    void  setSerializer(CommonSerializer serializer);
}