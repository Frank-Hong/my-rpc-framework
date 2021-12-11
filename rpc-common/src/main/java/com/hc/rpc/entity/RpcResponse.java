package com.hc.rpc.entity;

import com.hc.rpc.enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse<T> implements Serializable {
    private Integer statusCode;

    private String message;//状态码补充信息，作用是在失败时了解具体错误信息

    private T data;//响应数据

    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> response=new RpcResponse<>();
        //如果成功则不需要状态码补充信息
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code){
        RpcResponse<T> response=new RpcResponse<>();
        //如果失败则没有响应数据，但是应该有错误码和错误信息
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
