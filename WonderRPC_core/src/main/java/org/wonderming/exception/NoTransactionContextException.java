package org.wonderming.exception;

/**
 * @author wangdeming
 * @date 2019-12-06 14:38
 **/
public class NoTransactionContextException extends RuntimeException {

    public NoTransactionContextException(String message){
        super(message);
    }

}
