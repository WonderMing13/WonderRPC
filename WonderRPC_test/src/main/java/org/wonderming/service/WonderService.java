package org.wonderming.service;

import org.wonderming.annotation.ZookeeperLock;

/**
 * @className: WonderService
 * @package: org.wonderming.service
 * @author: wangdeming
 * @date: 2019-09-12 11:07
 **/
public interface WonderService {

    String getTest(String str);

    int getZookeeperLock();


}
