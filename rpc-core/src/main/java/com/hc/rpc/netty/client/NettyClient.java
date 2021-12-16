package com.hc.rpc.netty.client;

import com.hc.rpc.RpcClient;
import com.hc.rpc.codec.CommonDecoder;
import com.hc.rpc.codec.CommonEncoder;
import com.hc.rpc.entity.RpcRequest;
import com.hc.rpc.entity.RpcResponse;
import com.hc.rpc.enumeration.RpcError;
import com.hc.rpc.exception.RpcException;
import com.hc.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient implements RpcClient {
    private  static final Logger logger= LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private int port;
    private static EventLoopGroup group=new NioEventLoopGroup();
    private static CommonSerializer serializer;
    private static final Bootstrap bootstrap=new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.SO_KEEPALIVE,true)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new CommonDecoder())
                            .addLast(new CommonEncoder(serializer))
                            .addLast(new NettyClientHandler());
                }
            });


    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("客户端连接到服务器{}:{}",host,port);
            Channel channel = future.channel();
            if(channel!=null){
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()){
                        logger.info(String.format("客户端发送信息：%s",rpcRequest.toString()));
                    }
                    else{
                        logger.error("发送消息时有错误发生：",future1.cause());
                    }
                });
                channel.closeFuture().sync();//???
                AttributeKey<RpcResponse> key=AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse=channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生：",e);
        }
        return null;
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer=serializer;
    }
}





