package com.hc.rpc.api.rpc.test;

import com.hc.rpc.api.HelloObject;
import com.hc.rpc.api.HelloService;
import com.hc.rpc.socket.client.RpcClientProxy;
import com.hc.rpc.socket.client.SocketClient;

public class TestSocketClient {
    public static void main(String[] args) {
        //客户端不用创建RpcClient对象，RpcClient对象被封装在了框架内部，作为框架使用者只用在生成代理对象之后像使用本地方法一样调用api即可
        SocketClient socketClient = new SocketClient("127.0.0.1", 9000);
        RpcClientProxy proxy=new RpcClientProxy(socketClient);
        HelloService helloService=proxy.getProxy(HelloService.class);
        HelloObject object=new HelloObject(12,"This is a message");
        String res=helloService.hello(object);
        System.out.println(res);
    }
}
