package org.wonderming.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wangdeming
 * @date 2019-09-24 15:01
 **/
@Data
@Accessors(chain = true)
public class RpcResponse implements Serializable {
    private long responseId;
    private Object result;
    private Throwable error;
}
