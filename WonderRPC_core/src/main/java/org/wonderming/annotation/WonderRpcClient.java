package org.wonderming.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *     1.ElementType.TYPE 表示作用在接口上
 *     2.RetentionPolicy.RUNTIME 表示注解会在class字节码文件中存在,在运行时可以通过反射获取到
 *     3.@Documented 表示该注解将被包含在javadoc中
 * </p>
 * @author wangdeming
 * @date 2019-09-09 10:26
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WonderRpcClient {
    /**
     * RPCClient名称
     * @return String
     */
    String name() default "";
    /**
     * 是否开启@Primary注解
     * @return boolean
     */
    boolean primary() default true;

    /**
     * RPC调用的代理类
     * @return String
     */
    String proxyClass() default "";

    /**
     * RPC 消费者客户端 是否同步调用
     * @return boolean
     */
    boolean isSync() default true;

    /**
     * 同步调用时间设置 单位毫秒
     * @return int
     */
    int requestTimeout() default 3000;
}
