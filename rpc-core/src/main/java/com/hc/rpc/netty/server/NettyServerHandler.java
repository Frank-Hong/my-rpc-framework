package com.hc.rpc.netty.server;

import com.hc.rpc.entity.RpcRequest;
import com.hc.rpc.entity.RpcResponse;
import com.hc.rpc.registry.DefaultServiceRegistry;
import com.hc.rpc.registry.ServiceRegistry;
import com.hc.rpc.RequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger= LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler=new RequestHandler();
    private static ServiceRegistry serviceRegistry= new DefaultServiceRegistry();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        try {
            logger.info("服务器收到请求：{}",rpcRequest);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);//调用目标方法
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生：");
        cause.printStackTrace();
        ctx.close();
    }
}

