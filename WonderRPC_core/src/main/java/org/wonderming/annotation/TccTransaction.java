package org.wonderming.annotation;

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
    String confirmMethod();

    /**
     * 取消函数名称
     * @return 返回函数的名称
     */
    String cancelMethod();

    /**
     * 主事务是否异步执行confirm和cancel方法(主事务可以异步，分支事务不支持异步)
     * @return boolean 默认false
     */
    boolean isSync() default false;
}
