package org.wonderming.config.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author wangdeming
 * @date 2019-09-29 15:41
 **/
@Slf4j
public class MyThreadFactory {

    public static ThreadPoolExecutor getExecutor(){
        final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("rpc-pool-%d").build();
        return new ThreadPoolExecutor(4, 10, 600L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10240), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public static ExecutorService getSingleThreadPool(){
        return Executors.newSingleThreadExecutor();
    }

    public static ThreadPoolExecutor getCustomizeExecutor(){
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                //设置守护线程 主线程退出撕毁线程池
                thread.setDaemon(true);
                log.info("线程池创建线程 :" + thread);
                return thread;
            }
        };

        return new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                600L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                threadFactory,
                new ThreadPoolExecutor.DiscardPolicy()){

            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("准备执行: " + t.getName());
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("执行完毕: ");
            }

            @Override
            protected void terminated() {
                System.out.println("中断执行:");
            }

            @Override
            public void execute(Runnable command) {
                super.execute(wrap(command,clientTrace(),Thread.currentThread().getName()));
            }

            @Override
            public Future<?> submit(Runnable task) {
                return super.submit(wrap(task,clientTrace(),Thread.currentThread().getName()));
            }

            private Exception clientTrace(){
                return new Exception("Client Stack Trace");
            }

            private Runnable wrap(final Runnable task,final Exception clientTrace,String threadName){
                return new Runnable() {
                    @Override
                    public void run() {
                        try {
                            task.run();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
            }
        };
    }
}
