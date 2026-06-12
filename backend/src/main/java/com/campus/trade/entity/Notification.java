package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/Notification.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    private String title;
    
    private String content;
    
    private String type; // SYSTEM, REPORT, ORDER
    
    @TableField("related_id")
    private Long relatedId;
    
    @TableField("is_read")
    private Integer isRead;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}










