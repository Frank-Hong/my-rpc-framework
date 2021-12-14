package com.hc.rpc.codec;

import com.hc.rpc.entity.RpcRequest;
import com.hc.rpc.entity.RpcResponse;
import com.hc.rpc.enumeration.PackageType;
import com.hc.rpc.enumeration.RpcError;
import com.hc.rpc.exception.RpcException;
import com.hc.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 解码器的作用在于解析协议包，将传输的数据反序列回对象
 */
public class CommonDecoder extends ReplayingDecoder {
    private static final Logger logger= LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER=0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        int magic=byteBuf.readInt();
        if(magic!=MAGIC_NUMBER){
            logger.error("不识别的协议包：{}",magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode=byteBuf.readInt();
        //反序列化需要知道该反序列化为 RpcRequest还是RpcResponse类型
        Class<?> packageClass;
        if(packageCode== PackageType.REQUEST_PACK.getCode()){
            packageClass= RpcRequest.class;
        }
        else if(packageCode==PackageType.RESPONSE_PACK.getCode()){
            packageClass= RpcResponse.class;
        }
        else{
            logger.error("不识别的数据包：{}",packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serializerCode=byteBuf.readInt();
        CommonSerializer serializer=CommonSerializer.getByCode(serializerCode);
        if(serializer==null){
            logger.error("不识别的反序列化器：{}",serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length=byteBuf.readInt();
        byte[] bytes=new byte[length];
        byteBuf.readBytes(bytes);
        Object obj=serializer.deserialize(bytes,packageClass);
        out.add(obj);
    }
}
