package org.wonderming.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangdeming
 * @date 2019-09-24 15:01
 **/
@Data
@Accessors(chain = true)
public class RpcRequest implements Serializable {
    /**
     * RPC请求ID
     */
    private long requestId;
    /**
     * RPC请求体
     */
    private Object content;
    /**
     * 请求接口名称
     */
    private String interfaceName;
    /**
     * 请求方法名
     */
    private String methodName;
    /**
     * 请求方法参数类型列表
     */
    private Class<?>[] parameterTypes;
    /**
     * invoke参数列表
     */
    private Object[] param;

}
