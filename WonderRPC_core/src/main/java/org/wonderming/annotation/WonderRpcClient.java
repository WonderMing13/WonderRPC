package org.wonderming.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *     1.ElementType.TYPE 表示作用在接口上
 *     2.RetentionPolicy.RUNTIME 表示注解会在class字节码文件中存在,在运行时可以通过反射获取到
 *     3.@Documented 表示该注解将被包含在javadoc中
 * </p>
 * @className: WonderRpcClient
 * @package: org.wonderming.annotation
 * @author: wangdeming
 * @date: 2019-09-09 10:26
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WonderRpcClient {

    String name() default "";

    boolean primary() default true;
}
