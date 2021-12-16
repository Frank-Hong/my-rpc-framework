package com.hc.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hc.rpc.enumeration.RpcError;
import com.hc.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.rmi.Naming;
import java.util.List;

public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger= LoggerFactory.getLogger(NacosServiceRegistry.class);

    //nacos的默认端口是8848
    private static final String SERVER_ADDR="127.0.0.1:8848";
    private static final NamingService namingService;

    static {
        try {
            namingService= NamingFactory.createNamingService(SERVER_ADDR);
        }catch (NacosException e){
            logger.error("连接到nacos时有错误发生：",e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try{
            //告诉nacos哪台主机哪个端口可以提供哪个服务
           namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        }catch (NacosException e){
            logger.error("注册服务时有错误发生：",e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try{
            //获取能提供目标服务的所有主机
            List<Instance> instances=namingService.getAllInstances(serviceName);
            //目前没有实现负载均衡，固定选第一台主机
            Instance instance=instances.get(0);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        }catch (NacosException e){
            logger.error("获取服务时有错误发生：",e);
        }
        return null;
    }
}
