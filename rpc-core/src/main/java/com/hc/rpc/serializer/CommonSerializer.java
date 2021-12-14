package com.hc.rpc.serializer;

public interface CommonSerializer {
    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes,Class<?> clazz);

    //根据枚举类SerializerCode得到当前序列化器的序号
    //即每一个实现该接口的序列化器都会添加到枚举类SerializerCode中
    int getCode();

    //根据编号得到序列化器
    static CommonSerializer getByCode(int code){
        switch (code){
            case 1:return new JsonSerializer();
            default:return null;
        }
    }

}
