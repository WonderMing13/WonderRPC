package org.wonderming.annotation;

import org.springframework.context.annotation.Import;
import org.wonderming.registar.WonderRpcRegistrar;

import java.lang.annotation.*;

/**
 * @className: EnableWonderRpc
 * @package: org.wonderming.annotation
 * @author: wangdeming
 * @date: 2019-09-08 17:19
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(WonderRpcRegistrar.class)
public @interface EnableWonderRpc {

    /**
     * 扫描的包名
     * @return String[]数组
     */
    String[] basePackages() default {};
}
