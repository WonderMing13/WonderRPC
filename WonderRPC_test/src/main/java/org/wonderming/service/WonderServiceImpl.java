package org.wonderming.service;

import org.springframework.stereotype.Service;
import org.wonderming.annotation.ZookeeperLock;

/**
 * @className: WonderServiceImpl
 * @package: org.wonderming.service
 * @author: wangdeming
 * @date: 2019-09-12 11:16
 **/
@Service
public class WonderServiceImpl implements WonderService {

    private static int i = 0;

    @Override
    public String getTest(String str) {
        return "hi" + str;
    }

    @Override
    public int getZookeeperLock() {
        return i++;
    }

}
