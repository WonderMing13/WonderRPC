package org.wonderming.tcc.tccimpl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.wonderming.config.properties.TccProperties;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.type.TransactionStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangdeming
 * @date 2019-12-09 15:50
 **/
@Slf4j
@EnableConfigurationProperties(TccProperties.class)
public class DefaultTransactionRecovery {

    @Autowired
    private TccProperties tccProperties;

    private TransactionConfiguration transactionConfiguration;

    private static final String ROOT = "root";

    private static final String BRANCH = "branch";

    public TccProperties getTccProperties() {
        return tccProperties;
    }

    public void setTransactionConfiguration(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
    }

    public void startRecover() {
        log.info("Starting Recover....");
        List<Transaction> transactionList = Lists.newArrayList();
        long timeBefore = System.currentTimeMillis() - tccProperties.getRecoveryDuration() * 1000;
        final Map<String, List<Transaction>> map = transactionConfiguration.getResourceManager().doFindAllUnmodified(new Date(timeBefore));
        if (map.get(ROOT) != null) {
            final List<Transaction> rootTransactionList = map.get("root");
            rootTransactionList.forEach(transaction -> {
                final int update = transactionConfiguration.getResourceManager().update(transaction);
                if (update > 0) {
                    transactionList.add(transaction);
                }
            });
            for (Transaction transaction:transactionList) {
                if (transaction.getRetriedCount() > tccProperties.getMaxRetryCount()){
                    log.error(String.format("recover failed with max retry count, will not try again. txid:%s, status:%s, retried count:%d", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount()));
                    continue;
                }
                transaction.addRetriedCount();
                if (transaction.getStatus() == TransactionStatus.CONFIRM) {
                    transactionConfiguration.getResourceManager().update(transaction);
                    transaction.commit(transaction);
                } else if (transaction.getStatus() == TransactionStatus.CANCEL) {
                    transactionConfiguration.getResourceManager().update(transaction);
                    transaction.rollback(transaction);
                }
                final int delete = transactionConfiguration.getResourceManager().delete(transaction);
                log.info("result is {}", delete);
            }
        }
        log.info("end Recover....");
    }
}
