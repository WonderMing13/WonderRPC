package org.wonderming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wonderming.annotation.WonderRpcClient;
import org.wonderming.config.ZookeeperConfiguration;

import javax.annotation.Resource;

/**
 * @author wangdeming
 * @date 2019-09-10 15:58
 **/
@Service
public class TestServiceImpl implements ITestService {

    @Autowired
    private ZookeeperConfiguration zookeeperConfiguration;


    @Override
    public void getTest() {
        zookeeperConfiguration.create();
    }
}
