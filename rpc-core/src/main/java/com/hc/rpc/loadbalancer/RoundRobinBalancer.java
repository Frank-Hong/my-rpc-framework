package com.hc.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public class RoundRobinBalancer implements LoadBalancer{
    private int index=0;

    @Override
    public Instance select(List<Instance> instances) {
        index=(index+1)%instances.size();
        return instances.get(index);
    }
}
