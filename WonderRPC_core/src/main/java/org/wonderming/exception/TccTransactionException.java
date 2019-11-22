package org.wonderming.exception;

/**
 * @author wangdeming
 * @date 2019-11-22 13:15
 **/
public class TccTransactionException extends RuntimeException {

    public TccTransactionException(String message){
        super(message);
    }

    public TccTransactionException(String message,Throwable cause){
        super(message,cause);
    }
}
