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
    private long requestId;
    private Object content;

    public RpcRequest(){
        AtomicLong aid = new AtomicLong();
        requestId = aid.incrementAndGet();
    }
}
