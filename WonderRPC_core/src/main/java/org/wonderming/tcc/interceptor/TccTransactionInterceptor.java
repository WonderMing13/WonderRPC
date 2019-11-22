package org.wonderming.tcc.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wonderming.tcc.TransactionConfiguration;

/**
 * @author wangdeming
 * @date 2019-11-21 16:27
 **/
@Slf4j
@Component
public class TccTransactionInterceptor {

    @Autowired
    private TransactionConfiguration transactionConfiguration;


}
