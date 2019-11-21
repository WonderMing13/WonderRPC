package org.wonderming.service;

import org.wonderming.annotation.WonderRpcClient;
import org.wonderming.entity.DefaultFuture;
import org.wonderming.entity.RpcFuture;
import org.wonderming.entity.RpcResponse;

/**
 * @author wangdeming
 * @date 2019-09-09 10:34
 **/
@WonderRpcClient(name = "testWonder",proxyClass = "org.wonderming.service.WonderServiceImpl",isSync = false)
public interface ITestService {

    RpcFuture<RpcResponse> getTest(String str);

}
