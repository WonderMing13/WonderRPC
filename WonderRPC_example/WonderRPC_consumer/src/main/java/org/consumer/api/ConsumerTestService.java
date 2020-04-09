package org.consumer.api;

import org.wonderming.annotation.WonderRpcClient;
import org.wonderming.tcc.entity.TransactionContext;

/**
 * @author wangdeming
 **/
@WonderRpcClient(name = "hello",proxyClass = "org.provider.service.ProviderTestServiceImpl")
public interface ConsumerTestService {
    String test(TransactionContext transactionContext);
}
