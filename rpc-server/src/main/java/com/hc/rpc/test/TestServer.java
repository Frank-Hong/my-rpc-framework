package com.hc.rpc.test;

import com.hc.rpc.api.HelloService;
import com.hc.rpc.registry.DefaultServiceRegistry;
import com.hc.rpc.registry.ServiceRegistry;
import com.hc.rpc.server.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService=new HelloServiceImpl();
        ServiceRegistry serviceRegistry=new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        //不会把serviceRegistry和rpcServer绑定在一起，而是由框架使用者构造server时传入
        RpcServer rpcServer=new RpcServer(serviceRegistry);
        rpcServer.start(9000);//注册服务
    }
}
