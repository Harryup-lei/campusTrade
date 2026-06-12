package com.campus.trade.controller;




import com.campus.trade.common.Result;
import com.campus.trade.entity.Announcement;
import com.campus.trade.service.IAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/AnnouncementController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/announcements")
public class AnnouncementController {
    
    @Autowired
    private IAnnouncementService announcementService;
    
    /**
     * 获取启用的公告列表（前端首页使用）
     */
    @GetMapping("/active")
    public Result<List<Announcement>> getActiveAnnouncements(
            @RequestParam(defaultValue = "10") int limit) {
        List<Announcement> announcements = announcementService.getActiveAnnouncements(limit);
        return Result.success(announcements);
    }
    
    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        // 增加浏览次数
        announcementService.incrementViewCount(id);
        return Result.success(announcement);
    }
}










