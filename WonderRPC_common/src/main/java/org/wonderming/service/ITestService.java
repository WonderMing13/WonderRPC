package org.wonderming.service;

import org.wonderming.annotation.WonderRpcClient;

/**
 * @author wangdeming
 * @date 2019-09-09 10:34
 **/
@WonderRpcClient(name = "testWonder",proxyClass = "org.wonderming.service.WonderServiceImpl")
public interface ITestService {

    String getTest(String str);

}
