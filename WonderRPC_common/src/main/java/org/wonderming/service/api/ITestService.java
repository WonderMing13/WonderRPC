package org.wonderming.service.api;

import org.wonderming.annotation.TccTransaction;
import org.wonderming.annotation.WonderRpcClient;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcFuture;
import org.wonderming.entity.RpcResponse;
import org.wonderming.tcc.entity.TransactionContext;

/**
 * @author wangdeming
 * @date 2019-09-09 10:34
 **/
@WonderRpcClient(name = "testWonder",proxyClass = "org.wonderming.service.WonderServiceImpl")
public interface ITestService {

     String getTest(TransactionContext transactionContext,String str);

}
