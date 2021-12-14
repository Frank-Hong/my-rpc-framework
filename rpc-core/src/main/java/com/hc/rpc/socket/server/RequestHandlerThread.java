package com.hc.rpc.socket.server;

import com.hc.rpc.RequestHandler;
import com.hc.rpc.entity.RpcRequest;
import com.hc.rpc.entity.RpcResponse;
import com.hc.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


//和客户端打交道的类
public class RequestHandlerThread implements Runnable{
    private static final Logger logger= LoggerFactory.getLogger(RequestHandlerThread.class);

    private RequestHandler requestHandler=new RequestHandler();

    private ServiceRegistry serviceRegistry;

    private Socket socket;//由于需要和客户端打交道，所以需要socket对象

    public RequestHandlerThread(RequestHandler requestHandler, ServiceRegistry serviceRegistry, Socket socket) {
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.socket = socket;
    }

    @Override
    public void run() {
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())){
            RpcRequest rpcRequest= (RpcRequest) objectInputStream.readObject();
            //找到服务提供者
            String interfaceName=rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            //调用服务提供者的方法
            Object result = requestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();

        }catch(IOException | ClassNotFoundException e){
            logger.error("调用或发送时发生错误：{}",e);
        }
    }
}
