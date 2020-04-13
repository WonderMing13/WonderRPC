package org.consumer.api.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.consumer.entity.MerchantInfo;
import org.consumer.mapper.MerchantInfoMapper;
import org.consumer.api.service.IMerchantInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wonder
 * @since 2020-04-13
 */
@Service
public class MerchantInfoServiceImpl extends ServiceImpl<MerchantInfoMapper, MerchantInfo> implements IMerchantInfoService {

    @Resource
    private MerchantInfoMapper merchantInfoMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,rollbackFor = Exception.class)
    public void test() {
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setId(IdWorker.getId()).setMerchantName("a").setCreator("hjp").setCreatorTime(LocalDateTime.now());
        merchantInfoMapper.insert(merchantInfo);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS,rollbackFor = Exception.class)
    public void testWithException() {
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setId(IdWorker.getId()).setMerchantName("b").setCreator("xjx").setCreatorTime(LocalDateTime.now());
        merchantInfoMapper.insert(merchantInfo);
        throw new RuntimeException("error");
    }
}
