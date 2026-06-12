package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Banner;
import com.campus.trade.mapper.BannerMapper;
import com.campus.trade.service.IBannerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/BannerServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {
    
    @Override
    public List<Banner> getActiveBanners() {
        LocalDateTime now = LocalDateTime.now();
        
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, "ACTIVE")
                .and(w -> w.isNull(Banner::getStartTime)
                        .or()
                        .le(Banner::getStartTime, now))
                .and(w -> w.isNull(Banner::getEndTime)
                        .or()
                        .ge(Banner::getEndTime, now))
                .orderByAsc(Banner::getSortOrder)
                .orderByDesc(Banner::getCreateTime);
        
        return list(wrapper);
    }
    
    @Override
    public Page<Banner> getBannerPage(int page, int size, String status) {
        Page<Banner> bannerPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Banner::getStatus, status);
        }
        wrapper.orderByAsc(Banner::getSortOrder)
                .orderByDesc(Banner::getCreateTime);
        
        return page(bannerPage, wrapper);
    }
    
    @Override
    public boolean updateStatus(Long id, String status) {
        Banner banner = getById(id);
        if (banner == null) {
            return false;
        }
        banner.setStatus(status);
        return updateById(banner);
    }
    
    @Override
    public boolean updateSortOrder(Long id, Integer sortOrder) {
        Banner banner = getById(id);
        if (banner == null) {
            return false;
        }
        banner.setSortOrder(sortOrder);
        return updateById(banner);
    }
}










