package org.wonderming.exception;

/**
 * @author wangdeming
 * @date 2019-11-22 13:12
 **/
public class OptimisticLockException extends RuntimeException {

     public OptimisticLockException(String message){
         super(message);
     }
}
