package com.campus.trade.controller;




import com.campus.trade.common.Result;
import com.campus.trade.entity.Banner;
import com.campus.trade.service.IBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/BannerController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/banners")
public class BannerController {
    
    @Autowired
    private IBannerService bannerService;
    
    /**
     * 获取启用的轮播图列表（前端首页使用）
     */
    @GetMapping("/active")
    public Result<List<Banner>> getActiveBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return Result.success(banners);
    }
    
    /**
     * 获取轮播图详情
     */
    @GetMapping("/{id}")
    public Result<Banner> getBannerById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.error("轮播图不存在");
        }
        return Result.success(banner);
    }
}










