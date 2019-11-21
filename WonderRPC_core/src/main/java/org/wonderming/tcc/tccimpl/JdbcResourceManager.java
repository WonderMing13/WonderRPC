package org.wonderming.tcc.tccimpl;

import org.wonderming.tcc.entity.Transaction;
import org.wonderming.tcc.ResourceManager;

/**
 * @author wangdeming
 * @date 2019-11-18 22:00
 **/
public class JdbcResourceManager implements ResourceManager {
    @Override
    public int init() {
        return 0;
    }

    @Override
    public int create(Transaction transaction) {
        return 0;
    }

    @Override
    public int update(Transaction transaction) {
        return 0;
    }

    @Override
    public int delete(Transaction transaction) {
        return 0;
    }
}
