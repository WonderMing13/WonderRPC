package org.wonderming.service;

import org.wonderming.annotation.TccTransaction;
import org.wonderming.annotation.ZookeeperLock;
import org.wonderming.tcc.entity.TransactionContext;

/**
 * @className: WonderService
 * @package: org.wonderming.service
 * @author: wangdeming
 * @date: 2019-09-12 11:07
 **/
public interface WonderService {

    String getTest(TransactionContext transactionContext,String str);

    int getZookeeperLock();


}
