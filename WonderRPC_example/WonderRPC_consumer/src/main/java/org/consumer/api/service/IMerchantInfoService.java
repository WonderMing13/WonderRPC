package org.consumer.api.service;

import org.consumer.entity.MerchantInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wonder
 * @since 2020-04-13
 */
public interface IMerchantInfoService extends IService<MerchantInfo> {

     void test();

     void testWithException();
}
