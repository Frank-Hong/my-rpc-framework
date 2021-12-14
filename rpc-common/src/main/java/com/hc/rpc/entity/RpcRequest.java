package com.hc.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RpcRequest implements Serializable {
    public RpcRequest() {}

    private String interfaceName;

    private String methodName;

    private Object[] parameters;//调用目标方法时使用

    private Class<?>[] paraTypes;//找目标方法时使用
}
