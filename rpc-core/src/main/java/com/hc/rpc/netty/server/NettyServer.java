package com.hc.rpc.netty.server;

import com.hc.rpc.RpcServer;
import com.hc.rpc.codec.CommonDecoder;
import com.hc.rpc.codec.CommonEncoder;
import com.hc.rpc.enumeration.RpcError;
import com.hc.rpc.exception.RpcException;
import com.hc.rpc.provider.DefaultServiceProvider;
import com.hc.rpc.provider.ServiceProvider;
import com.hc.rpc.registry.NacosServiceRegistry;
import com.hc.rpc.registry.ServiceRegistry;
import com.hc.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;

public class NettyServer implements RpcServer {
    private static final Logger logger= LoggerFactory.getLogger(NettyServer.class);

    private final int port;
    private final String host;
    private CommonSerializer serializer;

    private final ServiceRegistry serviceRegistry=new NacosServiceRegistry();
    private final ServiceProvider serviceProvider=new DefaultServiceProvider();

    public NettyServer(String host, int port) {
        this.port = port;
        this.host = host;
    }

    @Override
    public void start() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,256)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            logger.error("启动服务器时有错误发生：",e);
        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.register(service);
        serviceRegistry.register(serviceClass.getCanonicalName(),new InetSocketAddress(host,port));
        start();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}


