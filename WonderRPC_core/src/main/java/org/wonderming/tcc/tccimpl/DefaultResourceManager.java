package org.wonderming.tcc.tccimpl;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wonderming.config.configuration.ServiceConfiguration;
import org.wonderming.tcc.ResourceManager;
import org.wonderming.tcc.entity.Transaction;
import org.wonderming.utils.ApplicationContextUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangdeming
 * @date 2019-11-22 14:33
 **/
public class DefaultResourceManager implements ResourceManager {

    private ServiceConfiguration getServiceConfiguration(){
        return ApplicationContextUtil.getApplicationContext().getBean(ServiceConfiguration.class);
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
    public int updateWithBranchError(Transaction transaction) {
        return getServiceConfiguration().doUpdateBranchError(transaction);
    }

    @Override
    public int updateWithRootError(Transaction transaction) {
        return getServiceConfiguration().doUpdateRootError(transaction);
    }

    @Override
    public int delete(Transaction transaction) {
        return getServiceConfiguration().doDelete(transaction);
    }

    @Override
    public int deleteWithBranchError(Transaction transaction) {
        return getServiceConfiguration().doDeleteWithBranchError(transaction);
    }

    @Override
    public int deleteWithRootError(Transaction transaction) {
        return getServiceConfiguration().doDeleteWithRootError(transaction);
    }

    @Override
    public Transaction findByXid(Transaction transaction) {
        return getServiceConfiguration().findByXid(transaction);
    }

    @Override
    public List<Transaction> doFindAllUnmodified(Date date) {
        return getServiceConfiguration().doFindAllUnmodified(date);
    }

    @Override
    public List<Transaction> doFindAllUnmodifiedWithBranchError(Date date) {
        return getServiceConfiguration().doFindAllUnmodifiedWithBranchError(date);
    }
}
