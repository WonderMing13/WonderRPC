package org.wonderming.tcc;

/**
 * 事务全局相关属性
 * @author wangdeming
 * @date 2019-11-18 21:24
 **/
public interface TransactionConfiguration {
    /**
     * 获取事务管理器TM
     * @return TransactionManager
     */
    TransactionManager getTransactionManager();
    /**
     * 获取资源管理器
     * @return ResourceManager
     */
    ResourceManager getResourceManager();


}
