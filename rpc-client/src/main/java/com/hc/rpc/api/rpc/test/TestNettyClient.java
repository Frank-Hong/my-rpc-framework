package com.hc.rpc.api.rpc.test;

import com.hc.rpc.RpcClient;
import com.hc.rpc.api.HelloObject;
import com.hc.rpc.api.HelloService;
import com.hc.rpc.netty.client.NettyClient;
import com.hc.rpc.socket.client.RpcClientProxy;

public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient client=new NettyClient("127.0.0.1",8888);
        RpcClientProxy rpcClientProxy=new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object=new HelloObject(12,"This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
