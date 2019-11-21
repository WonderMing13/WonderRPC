package org.wonderming.tcc.tccimpl;

import lombok.Getter;
import lombok.Setter;
import org.wonderming.tcc.ResourceManager;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.TransactionManager;

/**
 * @author wangdeming
 * @date 2019-11-18 21:55
 **/
public class TccTransactionConfiguration implements TransactionConfiguration {

    @Getter
    private TransactionManager transactionManager;

    @Setter
    @Getter
    private ResourceManager resourceManager;

    @Setter
    @Getter
    private String domain;

    public TccTransactionConfiguration(){
        this.transactionManager = new DefaultTransactionManager(this);
    }
}
