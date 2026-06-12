package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/INotificationService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Notification;

public interface INotificationService extends IService<Notification> {
    /**
     * 发送通知
     */
    void sendNotification(Long userId, String title, String content, String type, Long relatedId);
    
    /**
     * 发送举报处理通知
     */
    void sendReportNotification(Long reporterId, Long sellerId, String reportStatus, String result, String productTitle);
}










