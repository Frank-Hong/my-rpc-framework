package com.hc.rpc.test;

import com.hc.rpc.api.HelloService;
import com.hc.rpc.provider.DefaultServiceProvider;
import com.hc.rpc.provider.ServiceProvider;
import com.hc.rpc.serializer.KryoSerializer;
import com.hc.rpc.socket.server.SocketServer;

public class TestSocketServer {
    public static void main(String[] args) {
        HelloService helloService=new HelloServiceImpl();
        //不会把serviceRegistry和rpcServer绑定在一起，而是由框架使用者构造server时传入
        SocketServer rpcServer=new SocketServer("127.0.0.1", 8888);
        rpcServer.setSerializer(new KryoSerializer());
        rpcServer.publishService(helloService,HelloService.class);
    }
}
