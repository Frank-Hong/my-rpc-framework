package com.hc.rpc.test;

import com.hc.rpc.RpcServer;
import com.hc.rpc.api.HelloService;
import com.hc.rpc.netty.server.NettyServer;
import com.hc.rpc.serializer.KryoSerializer;

public class TestNettyServer {
    public static void main(String[] args) {
        //注册服务
        HelloService helloService=new HelloServiceImpl();
        RpcServer server=new NettyServer("127.0.0.1",8888);
        server.setSerializer(new KryoSerializer());
        server.publishService(helloService,HelloService.class);
    }
}
