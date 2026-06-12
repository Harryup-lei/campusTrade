package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客服咨询实体类
 */
/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/CustomerService.java
 * hangu: 38 行 | kelei: 0 行 | 本文件合计: 38 行
 */
@Data
@TableName("customer_service")
    public class CustomerService {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
                                                                                                                                private Long userId;
    
    private String userName;
    
    private String contactPhone;
    
    private String contactEmail;
    
    private String subject;
    
    private String content;
    
    private String category; // GENERAL, COMPLAINT, SUGGESTION, TECHNICAL
    
    private String status; // PENDING, PROCESSING, REPLIED, CLOSED
    
    private String reply;
    
    private LocalDateTime replyTime;
    
    private Long adminId;
    
    private String adminName;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    // 关联对象（非数据库字段）
    @TableField(exist = false)
    private User user;
}










