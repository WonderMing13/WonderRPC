package org.wonderming.interceptor;

import org.springframework.aop.framework.ProxyFactory;
import org.wonderming.service.TestService;
import org.wonderming.service.TestServiceImpl;

/**
 * @className: TestMethod
 * @package: org.wonderming.interceptor
 * @author: wangdeming
 * @date: 2019-09-10 16:12
 **/
public class TestMethod {

    public static void main(String[] args) {
        final ProxyFactory proxy = new ProxyFactory(new TestServiceImpl());
        proxy.addAdvice(new TestInterceptor());
        TestService testService = (TestService)proxy.getProxy();
        testService.getTest();
    }
}
