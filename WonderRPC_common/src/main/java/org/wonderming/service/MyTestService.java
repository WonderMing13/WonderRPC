package org.wonderming.service;

import org.wonderming.annotation.TccTransaction;

import java.util.concurrent.TimeoutException;

/**
 * @author wangdeming
 * @date 2019-11-24 12:29
 **/
public interface MyTestService {

    String test() throws TimeoutException;
}
