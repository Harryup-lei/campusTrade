package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Notification;
import com.campus.trade.mapper.NotificationMapper;
import com.campus.trade.service.INotificationService;
import org.springframework.stereotype.Service;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/NotificationServiceImpl.java
 * hangu: 37 行 | kelei: 0 行 | 本文件合计: 37 行
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements INotificationService {
    
    @Override
    public void sendNotification(Long userId, String title, String content, String type, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        save(notification);
    }
        @Override
    public void sendReportNotification(Long reporterId, Long sellerId, String reportStatus, String result, String productTitle) {
        // 给举报人发通知
        String reporterTitle = "";
        String reporterContent = "";
                                                                                                                                                                                                switch (reportStatus) {
            case "ACCEPTED":
                reporterTitle = "您的举报已受理";
                reporterContent = String.format("您对商品《%s》的举报已被管理员受理。处理说明：%s", productTitle, result);
                break;
            case "REJECTED":
                reporterTitle = "您的举报已驳回";
                reporterContent = String.format("您对商品《%s》的举报已被驳回。驳回原因：%s", productTitle, result);
                break;
            case "RESOLVED":
                reporterTitle = "您的举报已处理完成";
                reporterContent = String.format("您对商品《%s》的举报已处理完成。处理结果：%s 感谢您对平台的监督！", productTitle, result);
                break;
        }
        
        sendNotification(reporterId, reporterTitle, reporterContent, "REPORT", null);
        
        // 如果举报被受理或解决，给卖家发通知
        if ("ACCEPTED".equals(reportStatus) || "RESOLVED".equals(reportStatus)) {
            String sellerTitle = "您的商品收到举报";
            String sellerContent = "";
            
        if ("ACCEPTED".equals(reportStatus)) {
                sellerContent = String.format("您的商品《%s》收到举报并已被受理，请注意商品信息的真实性和准确性。", productTitle);
            } else {
                sellerContent = String.format("您的商品《%s》的举报已处理完成。处理结果：%s 请规范发布商品。", productTitle, result);
            }
            
            sendNotification(sellerId, sellerTitle, sellerContent, "REPORT", null);
        }
    }
}










