package com.campus.trade.controller;




import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Announcement;
import com.campus.trade.service.IAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/AdminAnnouncementController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/admin/announcements")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnnouncementController {
    
    @Autowired
    private IAnnouncementService announcementService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    
    /**
     * 分页查询公告
     */
    @GetMapping
    public Result<Page<Announcement>> getAnnouncementPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        Page<Announcement> announcementPage = announcementService.getAnnouncementPage(page, size, status, type);
        return Result.success(announcementPage);
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
        return Result.success(announcement);
    }
    
    /**
     * 新增公告
     */
    @PostMapping
    public Result<String> createAnnouncement(@RequestBody Announcement announcement) {
        if (announcement.getPriority() == null) {
            announcement.setPriority(0);
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus("ACTIVE");
        }
        if (announcement.getType() == null) {
            announcement.setType("NORMAL");
        }
        if (announcement.getViewCount() == null) {
            announcement.setViewCount(0);
        }
        boolean success = announcementService.save(announcement);
        if (success) {
            operationLogService.log("CREATE", "ANNOUNCEMENT", "发布公告：" + announcement.getTitle());
        }
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }
    
    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    public Result<String> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        Announcement existAnnouncement = announcementService.getById(id);
        if (existAnnouncement == null) {
            return Result.error("公告不存在");
        }
        announcement.setId(id);
        boolean success = announcementService.updateById(announcement);
        if (success) {
            operationLogService.log("UPDATE", "ANNOUNCEMENT", "更新公告：" + announcement.getTitle());
        }
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        boolean success = announcementService.removeById(id);
        if (success && announcement != null) {
            operationLogService.log("DELETE", "ANNOUNCEMENT", "删除公告：" + announcement.getTitle());
        }
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    /**
     * 更新公告状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = announcementService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
}










