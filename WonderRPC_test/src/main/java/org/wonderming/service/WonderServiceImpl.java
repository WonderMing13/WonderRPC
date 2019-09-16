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

    @Autowired
    private ApplicationContext applicationContext;



    @Override
    public void testWonder() {
        final Object bean = applicationContext.getBean("org.wonderming.service.TestService");
        final String name = bean.getClass().getName();
        System.out.println(name);
        System.out.println("hjp!");
    }
}
