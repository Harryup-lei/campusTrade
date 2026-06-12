package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Notification;
import com.campus.trade.entity.User;
import com.campus.trade.service.INotificationService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/NotificationController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {
    
    @Autowired
    private INotificationService notificationService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * 获取当前用户的通知列表
     */
    @GetMapping
    public Result<?> getMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Page<Notification> pageParam = new Page<>(page, size);
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.orderByDesc("create_time");
        
        IPage<Notification> notifications = notificationService.page(pageParam, queryWrapper);
        
        return Result.success(notifications);
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<?> getUnreadCount(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.eq("is_read", 0);
        
        long count = notificationService.count(queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", count);
        
        return Result.success(result);
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public Result<?> markAsRead(@PathVariable Long id, Authentication authentication) {
        Notification notification = notificationService.getById(id);
        
        if (notification == null) {
            return Result.error("通知不存在");
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        // 验证通知属于当前用户
        if (!notification.getUserId().equals(user.getId())) {
            return Result.error("无权访问");
        }
        
        notification.setIsRead(1);
        notificationService.updateById(notification);
        
        return Result.success("已标记为已读");
    }
    
    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<?> markAllAsRead(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.eq("is_read", 0);
        
        Notification update = new Notification();
        update.setIsRead(1);
        
        notificationService.update(update, queryWrapper);
        
        return Result.success("所有通知已标记为已读");
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteNotification(@PathVariable Long id, Authentication authentication) {
        Notification notification = notificationService.getById(id);
        
        if (notification == null) {
            return Result.error("通知不存在");
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        // 验证通知属于当前用户
        if (!notification.getUserId().equals(user.getId())) {
            return Result.error("无权访问");
        }
        
        notificationService.removeById(id);
        
        return Result.success("删除成功");
    }
}










