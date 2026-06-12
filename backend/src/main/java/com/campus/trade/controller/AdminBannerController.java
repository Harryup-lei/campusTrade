package com.campus.trade.controller;




import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Banner;
import com.campus.trade.service.IBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/AdminBannerController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/admin/banners")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBannerController {
    
    @Autowired
    private IBannerService bannerService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    
    /**
     * 分页查询轮播图
     */
    @GetMapping
    public Result<Page<Banner>> getBannerPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Page<Banner> bannerPage = bannerService.getBannerPage(page, size, status);
        return Result.success(bannerPage);
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
    
    /**
     * 新增轮播图
     */
    @PostMapping
    public Result<String> createBanner(@RequestBody Banner banner) {
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus("ACTIVE");
        }
        boolean success = bannerService.save(banner);
        
        if (success) {
            // 记录日志
            operationLogService.log("CREATE", "BANNER", "新增轮播图：" + banner.getTitle());
        }
        
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    /**
     * 更新轮播图
     */
    @PutMapping("/{id}")
    public Result<String> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        Banner existBanner = bannerService.getById(id);
        if (existBanner == null) {
            return Result.error("轮播图不存在");
        }
        existBanner.setLinkUrl(banner.getLinkUrl());
        existBanner.setSortOrder(banner.getSortOrder());
        boolean success = bannerService.updateById(existBanner);
        
        if (success) {
            // 记录日志
            operationLogService.log("UPDATE", "BANNER", "更新轮播图：" + existBanner.getTitle());
        }
        
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    /**
     * 删除轮播图
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteBanner(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        boolean success = bannerService.removeById(id);
        
        if (success && banner != null) {
            // 记录日志
            operationLogService.log("DELETE", "BANNER", "删除轮播图：" + banner.getTitle() + "(ID:" + id + ")");
        }
        
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    /**
     * 更新轮播图状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = bannerService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
    
    /**
     * 更新排序
     */
    @PutMapping("/{id}/sort")
    public Result<String> updateSortOrder(@PathVariable Long id, @RequestParam Integer sortOrder) {
        boolean success = bannerService.updateSortOrder(id, sortOrder);
        return success ? Result.success("排序更新成功") : Result.error("排序更新失败");
    }
}










