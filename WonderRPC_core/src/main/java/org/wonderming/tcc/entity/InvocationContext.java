package org.wonderming.tcc.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.wonderming.exception.InvokeException;
import org.wonderming.utils.ApplicationContextUtil;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author wangdeming
 * @date 2019-11-18 14:41
 **/
@Accessors(chain = true)
@Data
public class InvocationContext implements Serializable {

    /**
     * 目标类名
     */
    private String targetClassName;
    /**
     * confirm或者cancel函数的方法名
     */
    private String methodName;
    /**
     * 参数名列表
     */
    private Class<?>[] parameterTypes;
    /**
     * invoke参数列表
     */
    private Object[] param;
    /**
     * 执行提交或者回滚动作
     */
    void invoke(){
        try {
            final Class<?> targetClass = Class.forName(targetClassName);
            final Object bean = ApplicationContextUtil.getApplicationContext().getBean(targetClass);
            final Class<?> aClass = bean.getClass();
            final Method method = aClass.getMethod(methodName, parameterTypes);
            method.invoke(bean,param);
        } catch (Exception e) {
            throw new InvokeException("local invoke error",e);
        }
    }
}
