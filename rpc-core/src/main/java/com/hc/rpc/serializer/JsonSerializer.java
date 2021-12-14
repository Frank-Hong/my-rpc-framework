package com.hc.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hc.rpc.entity.RpcRequest;
import com.hc.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer{
    private static final Logger logger= LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生：{}",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            //有可能未能正确反序列化，重新检查一遍
            //所谓的“未正确反序列化”是指反序列之后参数和传过来的参数type不兼容，这时候需要重新反序列化这一参数
            //还是不明白为什么会出现反序列化错误？？？
            if(obj instanceof RpcRequest){
                obj=handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生：{}",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest=(RpcRequest) obj;
        for (int i = 0; i < rpcRequest.getParaTypes().length; i++) {
            Class<?> clazz=rpcRequest.getParaTypes()[i];
            //A.isAssignableFrom(B)：判断A是否为B的父类或实现的接口
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())){
                byte[] bytes=objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i]=objectMapper.readValue(bytes,clazz);//更改 rpcRequest中的参数类型
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        //valueof (String name) :根据名称获取枚举类中定义的常量值;要求字符串跟枚举的常量名必须一致;
        return SerializerCode.valueOf("JSON").getCode();
    }
}


