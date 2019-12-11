package org.wonderming.entity;

import lombok.Data;
import org.wonderming.exception.InvokeException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wangdeming
 * @date 2019-10-13 11:23
 **/
@Data
public class DefaultFuture implements RpcFuture<RpcResponse>{
    private final static ConcurrentHashMap<Long,DefaultFuture> ALL_DEFAULT_FUTURE = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private RpcResponse rpcResponse;

    public DefaultFuture(RpcRequest rpcRequest){
        ALL_DEFAULT_FUTURE.put(rpcRequest.getRequestId(),this);
    }

    @Override
    public RpcResponse get(){
        lock.lock();
        try {
            while (!isDone()){
                condition.await();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return this.rpcResponse;
    }

    @Override
    public RpcResponse get(int timeOut){
        //检测服务提供方是否成功返回了调用结果
        if (!isDone()){
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                //循环检测服务提供方是否成功返回了调用结果
                while (!isDone()){
                    //如果调用结果尚未返回，这里等待一段时间
                    condition.await(timeOut, TimeUnit.MILLISECONDS);
                    //时间到了也跳出
                    if (isDone() || System.currentTimeMillis() - start > timeOut){
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                lock.unlock();
            }
            // 如果调用结果仍未返回，则抛出超时异常
            if (!isDone()) {
                throw new InvokeException("remote invoke timeout/maybe remote invoke error");
            }
        }
        return this.rpcResponse;
    }

    public static void receive(RpcResponse rpcResponse){
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

    private boolean isDone(){
        return this.rpcResponse != null;
    }
}
