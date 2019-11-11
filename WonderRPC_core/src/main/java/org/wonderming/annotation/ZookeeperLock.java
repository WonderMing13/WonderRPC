package org.wonderming.annotation;

import java.lang.annotation.*;

/**
 * 放在controller层或者service层都可以
 * @author wangdeming
 * @date 2019-11-07 17:15
 **/
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZookeeperLock {
}
