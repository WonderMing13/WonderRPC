package org.wonderming.annotation;

import java.lang.annotation.*;

/**
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
}
