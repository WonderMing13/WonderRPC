package org.wonderming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
