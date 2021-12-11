package com.hc.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RpcRequest implements Serializable {
    private String interfaceName;

    private String methodName;

    private Object[] parameters;//调用目标方法时使用

    private Class<?>[] paraTypes;//找目标方法时使用
}
