package com.hc.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hc.rpc.loadbalancer.LoadBalancer;
import com.hc.rpc.utls.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery{
    private static final Logger logger= LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }


    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try{
            //获取能提供目标服务的所有主机
            List<Instance> instances= NacosUtil.getAllInstance(serviceName);
            //目前没有实现负载均衡，固定选第一台主机
            Instance instance= loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        }catch (NacosException e){
            logger.error("获取服务时有错误发生：",e);
        }
        return null;
    }
}
