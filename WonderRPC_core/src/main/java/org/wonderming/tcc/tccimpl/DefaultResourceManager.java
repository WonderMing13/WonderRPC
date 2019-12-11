package org.wonderming.tcc.tccimpl;

import org.apache.curator.framework.CuratorFramework;
import org.wonderming.config.configuration.ServiceConfiguration;
import org.wonderming.tcc.ResourceManager;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.utils.ApplicationContextUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangdeming
 * @date 2019-11-22 14:33
 **/
public class DefaultResourceManager implements ResourceManager {

    private ServiceConfiguration getServiceConfiguration(){
        return ApplicationContextUtil.getBean(ServiceConfiguration.class);
    }

    @Override
    public int create(Transaction transaction) {
        return getServiceConfiguration().doCreate(transaction);
    }

    @Override
    public int update(Transaction transaction) {
        return getServiceConfiguration().doUpdate(transaction);
    }

    @Override
    public int delete(Transaction transaction) {
        return getServiceConfiguration().doDelete(transaction);
    }

    @Override
    public Transaction findByXid(Transaction transaction) {
        return getServiceConfiguration().findByXid(transaction);
    }

    @Override
    public Map<String, List<Transaction>> doFindAllUnmodified(Date date) {
        return getServiceConfiguration().doFindAllUnmodified(date);
    }
}
