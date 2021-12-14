package com.hc.rpc.test;

import com.hc.rpc.RpcServer;
import com.hc.rpc.api.HelloService;
import com.hc.rpc.netty.server.NettyServer;
import com.hc.rpc.registry.DefaultServiceRegistry;
import com.hc.rpc.registry.ServiceRegistry;

public class TestNettyServer {
    public static void main(String[] args) {
        //注册服务
        HelloService helloService=new HelloServiceImpl();
        ServiceRegistry serviceRegistry=new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer server=new NettyServer();
        server.start(8888);
    }
}
