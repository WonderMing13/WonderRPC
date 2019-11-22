package org.wonderming.tcc.tccimpl;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.wonderming.tcc.ResourceManager;
import org.wonderming.tcc.TransactionConfiguration;
import org.wonderming.tcc.TransactionManager;

/**
 * @author wangdeming
 * @date 2019-11-18 21:55
 **/
@Data
public class DefaultTransactionConfiguration implements TransactionConfiguration {

    private TransactionManager transactionManager;

    private ResourceManager resourceManager;
}
