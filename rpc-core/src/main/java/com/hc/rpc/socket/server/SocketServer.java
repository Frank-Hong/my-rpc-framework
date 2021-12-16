package com.hc.rpc.socket.server;

import com.hc.rpc.RequestHandler;
import com.hc.rpc.RpcServer;
import com.hc.rpc.provider.DefaultServiceProvider;
import com.hc.rpc.provider.ServiceProvider;
import com.hc.rpc.registry.NacosServiceRegistry;
import com.hc.rpc.registry.ServiceRegistry;
import com.hc.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {
    private final ExecutorService threadPool;

    private static final Logger logger= LoggerFactory.getLogger(SocketServer.class);

    private final String host;
    private final int port;
    private final ServiceProvider serviceProvider=new DefaultServiceProvider();
    private final ServiceRegistry serviceRegistry=new NacosServiceRegistry();
    private RequestHandler requestHandler=new RequestHandler();
    private CommonSerializer serializer;


    public SocketServer(String host, int port){
        this.host = host;
        this.port = port;
        int corePoolSize=5;
        int maximumPoolSize=50;
        long keepAliveTime=60;
        BlockingQueue<Runnable> workingQueue=new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory= Executors.defaultThreadFactory();
        threadPool=new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.SECONDS,workingQueue,threadFactory);
        start();
    }

    //start之后serviceProvider里的服务均可调用
    @Override
    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            Socket socket;
            //可以不断注册新的服务
            while((socket=serverSocket.accept())!=null){
                logger.info("客户端连接成功！IP为"+socket.getInetAddress());
                //这里将服务细节抽离出去
                threadPool.execute(new RequestHandlerThread(requestHandler,serviceProvider,socket));
            }
        }catch (IOException e){
            logger.error("连接错误：",e);
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        serviceProvider.register(service);
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
