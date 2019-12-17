package org.wonderming.utils;

import org.wonderming.exception.NoTransactionContextException;

/**
 * @author wangdeming
 * @date 2019-12-06 14:43
 **/
public class AssertUtil {

    public static void isTrue(boolean expression){
        if (!expression){
            throw new NoTransactionContextException("No TransactionContext in consumer");
        }
    }
}
