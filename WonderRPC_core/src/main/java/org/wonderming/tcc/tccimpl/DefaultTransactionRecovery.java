package org.wonderming.tcc.tccimpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.wonderming.config.properties.TccProperties;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.entity.Transaction;

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

    public TccProperties getTccProperties() {
        return tccProperties;
    }

    public void setTransactionConfiguration(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
    }

    public void startRecover() {
        log.info("Starting Recover....");
        long timeBefore=System.currentTimeMillis() - tccProperties.getRecoveryDuration()*1000;
        final Map<String, List<Transaction>> map = transactionConfiguration.getResourceManager().doFindAllUnmodified(new Date(timeBefore));
        System.out.println(map.get("root"));
        System.out.println(map.get("branch"));
        log.info("end Recover....");
    }


}
