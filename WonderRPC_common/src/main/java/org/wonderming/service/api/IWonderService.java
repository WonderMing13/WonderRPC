package org.wonderming.service.api;

import org.wonderming.annotation.TccTransaction;
import org.wonderming.annotation.WonderRpcClient;
import org.wonderming.tcc.entity.TransactionContext;

/**
 * @author wangdeming
 * @date 2019-11-16 16:12
 **/
@WonderRpcClient(name = "testTwo",proxyClass = "org.wonderming.service.TestServiceImpl")
public interface IWonderService {

    String getWonder(TransactionContext transactionContext, String str);
}