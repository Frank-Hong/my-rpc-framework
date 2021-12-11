package com.hc.rpc.client;

import com.hc.rpc.entity.RpcRequest;
import com.hc.rpc.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {
    private String host;

    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //T是客户端调用的接口，生成代理对象之后客户端就可以像使用本地方法一样调用接口方法
    //this表示本类包含了回调逻辑，执行代理对象方法时会调用回调方法，即发送发送rpc请求并接收结果
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest=RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paraTypes(method.getParameterTypes())
                .build();
        RpcClient rpcClient=new RpcClient();
        return ((RpcResponse) rpcClient.sendRequest(rpcRequest, host, port)).getData();

    }
}
