package org.wonderming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wonderming.annotation.WonderRpcClient;

import javax.annotation.Resource;

/**
 * @className: TestServiceImpl
 * @package: org.wonderming.service
 * @author: wangdeming
 * @date: 2019-09-10 15:58
 **/
@Service
public class TestServiceImpl implements ITestService {

    @Override
    public void getTest() {
        System.out.println("xjx!");
    }
}
