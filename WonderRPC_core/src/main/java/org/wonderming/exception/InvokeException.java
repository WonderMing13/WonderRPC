package org.wonderming.exception;

/**
 * @author wangdeming
 * @date 2019-12-10 16:23
 **/
public class InvokeException extends RuntimeException  {

    public InvokeException(String message){
        super(message);
    }

    public InvokeException(String message, Throwable cause){
        super(message, cause);
    }
}
