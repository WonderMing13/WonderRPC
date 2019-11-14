package org.wonderming.entity;

import java.util.concurrent.TimeoutException;

/**
 * @author wangdeming
 * @date 2019-11-13 17:56
 **/
public interface RpcFuture<T> {

    T get();

    T get(int requestTimeout) throws TimeoutException;
}
