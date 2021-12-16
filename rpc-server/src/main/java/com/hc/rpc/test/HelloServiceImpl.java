package com.hc.rpc.test;

import com.hc.rpc.annotation.Service;
import com.hc.rpc.api.HelloObject;
import com.hc.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HelloServiceImpl implements HelloService {
    private static final Logger logger= LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到：{}",object.getMessage());
        return "这是调用的返回值，id="+object.getId();
    }
}
