package com.hc.rpc.test;

import com.hc.rpc.annotation.ServiceScan;
import com.hc.rpc.api.HelloService;
import com.hc.rpc.provider.DefaultServiceProvider;
import com.hc.rpc.provider.ServiceProvider;
import com.hc.rpc.serializer.KryoSerializer;
import com.hc.rpc.socket.server.SocketServer;

@ServiceScan
public class TestSocketServer {
    public static void main(String[] args) {
        SocketServer rpcServer=new SocketServer("127.0.0.1", 8888);
        rpcServer.setSerializer(new KryoSerializer());
        rpcServer.start();
    }
}
