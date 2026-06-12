package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Want;
import com.campus.trade.entity.WantOffer;
import com.campus.trade.mapper.WantOfferMapper;
import com.campus.trade.service.IWantOfferService;
import com.campus.trade.service.IWantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/WantOfferServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class WantOfferServiceImpl extends ServiceImpl<WantOfferMapper, WantOffer> implements IWantOfferService {
    
    @Autowired
    private IWantService wantService;
    
    @Override
    public boolean submitOffer(WantOffer offer) {
        // 设置默认状态
        if (offer.getStatus() == null) {
            offer.setStatus("PENDING");
        }
        // 手动设置时间（防止自动填充不生效）
        if (offer.getCreateTime() == null) {
            offer.setCreateTime(LocalDateTime.now());
        }
        if (offer.getUpdateTime() == null) {
            offer.setUpdateTime(LocalDateTime.now());
        }
        return save(offer);
    }
    
    @Override
    public List<WantOffer> getOffersByWantId(Long wantId) {
        LambdaQueryWrapper<WantOffer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WantOffer::getWantId, wantId)
                .orderByDesc(WantOffer::getCreateTime);
        return list(wrapper);
    }
    
    @Override
    public Page<WantOffer> getMyOffers(Long sellerId, int page, int size) {
        Page<WantOffer> offerPage = new Page<>(page, size);
        LambdaQueryWrapper<WantOffer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WantOffer::getSellerId, sellerId)
                .orderByDesc(WantOffer::getCreateTime);
        return page(offerPage, wrapper);
    }
    
    @Override
    public Page<WantOffer> getReceivedOffers(Long buyerId, int page, int size) {
        // 先获取这个买家的所有求购
        LambdaQueryWrapper<Want> wantWrapper = new LambdaQueryWrapper<>();
        wantWrapper.eq(Want::getUserId, buyerId);
        List<Want> wants = wantService.list(wantWrapper);
        
        if (wants.isEmpty()) {
            return new Page<>(page, size);
        }
        
        // 获取这些求购的所有出价
        List<Long> wantIds = wants.stream().map(Want::getId).toList();
        
        Page<WantOffer> offerPage = new Page<>(page, size);
        LambdaQueryWrapper<WantOffer> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(WantOffer::getWantId, wantIds)
                .orderByDesc(WantOffer::getCreateTime);
        return page(offerPage, wrapper);
    }
    
    @Override
    public boolean acceptOffer(Long offerId, Long buyerId) {
        WantOffer offer = getById(offerId);
        if (offer == null) {
            return false;
        }
        
        // 验证这个出价对应的求购是否属于当前用户
        Want want = wantService.getById(offer.getWantId());
        if (want == null || !want.getUserId().equals(buyerId)) {
            return false;
        }
        
        offer.setStatus("ACCEPTED");
        return updateById(offer);
    }
    
    @Override
    public boolean rejectOffer(Long offerId, Long buyerId) {
        WantOffer offer = getById(offerId);
        if (offer == null) {
            return false;
        }
        
        // 验证这个出价对应的求购是否属于当前用户
        Want want = wantService.getById(offer.getWantId());
        if (want == null || !want.getUserId().equals(buyerId)) {
            return false;
        }
        
        offer.setStatus("REJECTED");
        return updateById(offer);
    }
}










