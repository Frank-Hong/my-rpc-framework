package com.hc.rpc.test;

import com.hc.rpc.RpcServer;
import com.hc.rpc.annotation.ServiceScan;
import com.hc.rpc.api.HelloService;
import com.hc.rpc.netty.server.NettyServer;
import com.hc.rpc.serializer.KryoSerializer;

@ServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer server=new NettyServer("127.0.0.1",8888);
        server.setSerializer(new KryoSerializer());
        server.start();
    }
}
