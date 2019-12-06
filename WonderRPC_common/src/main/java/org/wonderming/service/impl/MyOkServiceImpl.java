package org.wonderming.service.impl;

import org.springframework.stereotype.Service;
import org.wonderming.service.MyOkService;
import org.wonderming.tcc.entity.TransactionContext;

/**
 * @author wangdeming
 * @date 2019-11-29 16:20
 **/
@Service
public class MyOkServiceImpl implements MyOkService {

    @Override
    public String  ok(TransactionContext transactionContext,String str) {
        return "ok" + str;
    }
}
