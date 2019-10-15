package org.wonderming.entity;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wangdeming
 * @date 2019-10-13 11:23
 **/
@Data
public class DefaultFuture {
    private final static ConcurrentHashMap<Long,DefaultFuture> ALL_DEFAULT_FUTURE = new ConcurrentHashMap<Long, DefaultFuture>();

    private final Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private RpcResponse rpcResponse;

    public DefaultFuture(RpcRequest rpcRequest){
        ALL_DEFAULT_FUTURE.put(rpcRequest.getRequestId(),this);
    }

    public RpcResponse get(){
        lock.lock();
        try {
            while (!done()){
                condition.await();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return this.rpcResponse;
    }

    public static void recive(RpcResponse rpcResponse){
        DefaultFuture defaultFuture = ALL_DEFAULT_FUTURE.get(rpcResponse.getResponseId());
        if (defaultFuture != null){
            Lock lock = defaultFuture.lock;
            lock.lock();
            try{
                defaultFuture.setRpcResponse(rpcResponse);
                defaultFuture.condition.signal();
                ALL_DEFAULT_FUTURE.remove(rpcResponse.getResponseId());
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    private boolean done(){
        return this.rpcResponse != null;
    }
}
