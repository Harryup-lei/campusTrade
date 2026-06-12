package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/Report.java
 * hangu: 39 行 | kelei: 0 行 | 本文件合计: 39 行
 */
@Data
@TableName("report")
    public class Report {
    @TableId(type = IdType.AUTO)
    private Long id;
    
                                @TableField("product_id")
                private Long productId;
    
    @TableField("reporter_id")
    private Long reporterId;
    
    private String reason; // 举报原因
    
    private String description; // 详细描述
    
    private String status; // 状态：PENDING, ACCEPTED, REJECTED, RESOLVED
    
    @TableField("handler_id")
    private Long handlerId; // 处理人ID
    
    @TableField("handle_result")
    private String handleResult; // 处理结果
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("handle_time")
    private LocalDateTime handleTime;
    
    // 关联字段（不存储在数据库中）
    @TableField(exist = false)
    private Product product; // 被举报的商品
    
    @TableField(exist = false)
    private User reporter; // 举报人
    
    @TableField(exist = false)
    private User handler; // 处理人
}










