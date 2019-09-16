package org.wonderming.service;

import org.wonderming.annotation.WonderRpcClient;

/**
 * @className: TestService
 * @package: org.wonderming.service
 * @author: wangdeming
 * @date: 2019-09-09 10:34
 **/
@WonderRpcClient(name = "testWonder")
public interface ITestService {

    public void getTest();
}
