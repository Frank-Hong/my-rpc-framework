package com.hc.rpc.server;

import com.hc.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServer {
    private final ExecutorService threadPool;

    private static final Logger logger= LoggerFactory.getLogger(RpcServer.class);

    private final ServiceRegistry serviceRegistry;

    private RequestHandler requestHandler=new RequestHandler();

    public RpcServer(ServiceRegistry serviceRegistry){
        int corePoolSize=5;
        int maximumPoolSize=50;
        long keepAliveTime=60;
        BlockingQueue<Runnable> workingQueue=new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory= Executors.defaultThreadFactory();
        threadPool=new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.SECONDS,workingQueue,threadFactory);
        this.serviceRegistry=serviceRegistry;
    }

    //start之后serviceRegistry里的服务均可调用
    public void start(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            Socket socket;
            //可以不断注册新的服务
            while((socket=serverSocket.accept())!=null){
                logger.info("客户端连接成功！IP为"+socket.getInetAddress());
                //这里将服务细节抽离出去
                threadPool.execute(new RequestHandlerThread(requestHandler,serviceRegistry,socket));
            }
        }catch (IOException e){
            logger.error("连接错误：",e);
        }
    }
}
