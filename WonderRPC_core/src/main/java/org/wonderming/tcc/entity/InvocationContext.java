package org.wonderming.tcc.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangdeming
 * @date 2019-11-18 14:41
 **/
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
    private String[] parameterTypes;
    /**
     * invoke参数列表
     */
    private Object[] param;
    /**
     * 执行提交或者回滚动作
     */
    void invoke(){}
}
