package org.consumer.api;

import org.wonderming.annotation.WonderRpcClient;
import org.wonderming.tcc.entity.TransactionContext;

/**
 * @author wangdeming
 **/
@WonderRpcClient(name = "test",proxyClass = "org.provider.service.impl.ProviderOneTestServiceImpl")
public interface ConsumerOneTestService {
    String test(TransactionContext transactionContext);
}
