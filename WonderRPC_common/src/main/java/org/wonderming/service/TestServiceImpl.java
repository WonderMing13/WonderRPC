package org.wonderming.service;

import org.springframework.stereotype.Service;

/**
 * @className: TestServiceImpl
 * @package: org.wonderming.service
 * @author: wangdeming
 * @date: 2019-09-10 15:58
 **/
@Service
public class TestServiceImpl implements TestService {

    @Override
    public String getTest() {
        return "Let Go XJX & HJP";
    }
}
