package org.provider.service;

import org.springframework.stereotype.Service;

/**
 * @author wangdeming
 **/
@Service
public class ProviderHelloServiceImpl implements ProviderHelloService {

    @Override
    public String test() {
        return "huang jian peng";
    }
}
