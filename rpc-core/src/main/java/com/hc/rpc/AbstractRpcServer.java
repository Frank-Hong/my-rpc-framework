package com.hc.rpc;

import com.hc.rpc.annotation.Service;
import com.hc.rpc.annotation.ServiceScan;
import com.hc.rpc.enumeration.RpcError;
import com.hc.rpc.exception.RpcException;
import com.hc.rpc.provider.ServiceProvider;
import com.hc.rpc.registry.ServiceRegistry;
import com.hc.rpc.utls.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;


    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.register(service,serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

    public void scanService(){
        String mainClassName= ReflectUtil.getStackTrace();
        Class<?> startClass;
        try{
            startClass=Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少@ServiceScan注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        }catch (ClassNotFoundException e){
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        //ServiceScan扫描value值对应的包，如果value为空则默认扫描启动类所在包
        String basePackage=startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(Service.class)) {
                //Service的name定义为服务的名称，若name为空则默认名称为服务完整类名
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }
}
