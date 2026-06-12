package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/IWantOfferService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.WantOffer;

import java.util.List;

public interface IWantOfferService extends IService<WantOffer> {
    
    /**
     * 提交出价
     */
    boolean submitOffer(WantOffer offer);
    
    /**
     * 获取某个求购的所有出价
     */
    List<WantOffer> getOffersByWantId(Long wantId);
    
    /**
     * 获取我的出价列表（卖家视角）
     */
    Page<WantOffer> getMyOffers(Long sellerId, int page, int size);
    
    /**
     * 获取我收到的出价（买家视角）
     */
    Page<WantOffer> getReceivedOffers(Long buyerId, int page, int size);
    
    /**
     * 接受出价
     */
    boolean acceptOffer(Long offerId, Long buyerId);
    
    /**
     * 拒绝出价
     */
    boolean rejectOffer(Long offerId, Long buyerId);
}










