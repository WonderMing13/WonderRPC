package org.consumer.api;

import org.wonderming.annotation.WonderRpcClient;

/**
 * @author wangdeming
 **/
@WonderRpcClient(name = "hello",proxyClass = "org.provider.service.ProviderHelloServiceImpl")
public interface ConsumerHelloService {
    String test();
}
