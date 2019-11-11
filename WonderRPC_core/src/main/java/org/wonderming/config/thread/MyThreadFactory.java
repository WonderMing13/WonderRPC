package org.wonderming.config.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author wangdeming
 * @date 2019-09-29 15:41
 **/
public class MyThreadFactory {

    public static ThreadPoolExecutor getExecutor(){
        final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();
        return new ThreadPoolExecutor(4, 10, 600L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10240), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public static ExecutorService getSingleThreadPool(){
        return Executors.newSingleThreadExecutor();
    }
}
