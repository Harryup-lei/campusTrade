package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/IBannerService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Banner;

import java.util.List;

public interface IBannerService extends IService<Banner> {
    
    /**
     * 获取启用的轮播图列表（用于前端展示）
     */
    List<Banner> getActiveBanners();
    
    /**
     * 分页查询轮播图（管理后台）
     */
    Page<Banner> getBannerPage(int page, int size, String status);
    
    /**
     * 更新轮播图状态
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * 更新排序
     */
    boolean updateSortOrder(Long id, Integer sortOrder);
}










