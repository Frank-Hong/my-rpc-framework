package com.hc.rpc;

import com.hc.rpc.entity.RpcRequest;
import com.hc.rpc.entity.RpcResponse;
import com.hc.rpc.enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//调用远程方法的工具
public class RequestHandler {
    private static final Logger logger= LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(RpcRequest rpcRequest,Object service){
        Object result=null;
        try{
            result=invokeTargetMethod(rpcRequest,service);
            logger.info("服务{}:成功调用方法：{}",rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
        }catch (IllegalAccessException | InvocationTargetException e){
            logger.error("调用或发送时发生错误：",e);
        }
        return result;
    }

    //利用反射调用目标方法
    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) throws IllegalAccessException, InvocationTargetException {
        Method method;
        try{
            method=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParaTypes());
        }catch (NoSuchMethodException e){
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service,rpcRequest.getParameters());
    }
}


