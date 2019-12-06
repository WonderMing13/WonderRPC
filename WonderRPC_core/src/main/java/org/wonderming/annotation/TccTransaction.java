package org.wonderming.annotation;

import org.wonderming.tcc.type.MethodType;
import org.wonderming.tcc.type.PropagationType;

import java.lang.annotation.*;

/**
 * Tcc事务的Try方法
 * @author wangdeming
 * @date 2019-11-16 22:35
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TccTransaction {

    /**
     * 确认函数名称
     * @return 返回函数的名称
     */
    String confirmMethod() default "";

    /**
     * 取消函数名称
     * @return 返回函数的名称
     */
    String cancelMethod() default "";

    /**
     * 事务属性
     * @return MethodType
     */
    MethodType type() default MethodType.ROOT;


}
