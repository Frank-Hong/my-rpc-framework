package com.hc.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hc.rpc.entity.RpcRequest;
import com.hc.rpc.entity.RpcResponse;
import com.hc.rpc.enumeration.SerializerCode;
import com.hc.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements CommonSerializer{
    private static final Logger logger= LoggerFactory.getLogger(KryoSerializer.class);

    //kryo是非线程安全的，需要和ThreadLocal搭配使用
    private static final ThreadLocal<Kryo> kryoThreadLocal =ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        //kryo.setReferences(true);
        kryo.setRegistrationRequired(false);//对于多机器部署的情况，建议关闭注册
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //Kryo提供的类
            Output output = new Output(byteArrayOutputStream)){

            //为每一个线程都提供一个Kryo实例
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (Exception e){
            logger.error("序列化时有错误发生：",e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(bytes);
            Input input =new Input(byteArrayInputStream)){

            Kryo kryo=kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        }catch(Exception e){
            logger.error("反序列化时有错误发生：",e);
            throw new SerializeException("反序列化时有错误发生：");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.KRYO.getCode();
    }
}
