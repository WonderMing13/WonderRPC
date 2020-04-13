package org.consumer.controller;


import org.consumer.api.service.ConsumerTccService;
import org.consumer.api.service.IMerchantInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wonder
 * @since 2020-04-13
 */
@RestController
@RequestMapping("/local")
public class LocalTransactionController {

    @Autowired
    private ConsumerTccService consumerTccService;

    @GetMapping(value = "/testTransaction")
    public void getTransaction(){
        consumerTccService.testTransaction();
    }
}
