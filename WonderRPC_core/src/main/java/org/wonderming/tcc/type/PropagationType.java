package org.wonderming.tcc.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangdeming
 * @date 2019-11-21 14:27
 **/
@AllArgsConstructor
public enum  PropagationType {
    /**
     * 表示当前方法必须在一个事务中运行。如果一个现有事务正在进行中，该方法将在那个事务中运行，否则就要开始一个新事务
     */
    PROPAGATION_REQUIRES(0),
    /**
     * 表示当前方法必须在它自己的事务里运行。一个新的事务将被启动，而且如果有一个现有事务在运行的话，则将在这个方法运行期间被挂起
     */
    PROPAGATION_REQUIRES_NEW(1),
    /**
     * 表示当前方法不需要事务性上下文，但是如果有一个事务已经在运行的话，它也可以在这个事务里运行
     */
    PROPAGATION_SUPPORTS(2),
    /**
     * 表示该方法不应该在一个事务中运行。如果一个现有事务正在进行中，它将在该方法的运行期间被挂起
     */
    PROPAGATION_NOT_SUPPORTED(3),
    /**
     * 表示当前的方法不应该在一个事务中运行。如果一个事务正在进行，则会抛出一个异常
     */
    PROPAGATION_NEVER(4),
    /**
     * 表示如果当前正有一个事务在进行中，则该方法应当运行在一个嵌套式事务中。被嵌套的事务可以独立于封装事务进行提交或回滚。如果封装事务不存在，行为就像PROPAGATION_REQUIRES一样
     */
    PROPAGATION_NESTED(5),
    /**
     * 	表示该方法必须运行在一个事务中。如果当前没有事务正在发生，将抛出一个异常
     */
    PROPAGATION_MANDATORY(6);

    @Getter
    private int id;
}
