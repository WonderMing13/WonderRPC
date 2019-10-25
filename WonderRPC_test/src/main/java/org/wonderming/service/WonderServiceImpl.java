package org.wonderming.service;

import org.springframework.stereotype.Service;

/**
 * @className: WonderServiceImpl
 * @package: org.wonderming.service
 * @author: wangdeming
 * @date: 2019-09-12 11:16
 **/
@Service
public class WonderServiceImpl implements WonderService {


    @Override
    public String getTest(String str) {
        return "hi" + str;
    }

}
