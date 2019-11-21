package org.wonderming.tcc.tccimpl;

import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.entity.TransactionContext;
import org.wonderming.tcc.type.TransactionStatus;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.TransactionManager;

/**
 * @author wangdeming
 * @date 2019-11-18 21:57
 **/
public class DefaultTransactionManager implements TransactionManager {

    private TransactionConfiguration transactionConfiguration;

    public DefaultTransactionManager(TransactionConfiguration transactionConfiguration){
        this.transactionConfiguration = transactionConfiguration;
    }

    @Override
    public void begin() {

    }

    @Override
    public void begin(TransactionContext transactionContext) {

    }

    @Override
    public void changeTransactionStatus(TransactionStatus transactionStatus) {

    }

    @Override
    public Transaction getCurrentTransaction() {
        return null;
    }

    @Override
    public void commit(boolean isSync) {

    }

    @Override
    public void rollback(boolean isSync) {

    }
}
