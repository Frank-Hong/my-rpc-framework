package com.hc.rpc.test;

import com.hc.rpc.api.HelloService;
import com.hc.rpc.registry.DefaultServiceRegistry;
import com.hc.rpc.registry.ServiceRegistry;
import com.hc.rpc.socket.server.SocketServer;

public class TestSocketServer {
    public static void main(String[] args) {
        HelloService helloService=new HelloServiceImpl();
        ServiceRegistry serviceRegistry=new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        //不会把serviceRegistry和rpcServer绑定在一起，而是由框架使用者构造server时传入
        SocketServer rpcServer=new SocketServer(serviceRegistry);
        rpcServer.start(9000);//注册服务
    }
}
