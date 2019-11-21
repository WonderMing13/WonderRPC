package org.wonderming.service;

import org.springframework.stereotype.Service;

/**
 * @author wangdeming
 * @date 2019-11-16 16:01
 **/
@Service
public class TestServiceImpl implements TestService {

    @Override
    public String getWonder(String str) {
        return "hi" + str;
    }
}
