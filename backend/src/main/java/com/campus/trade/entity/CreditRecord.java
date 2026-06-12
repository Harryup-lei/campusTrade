package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/CreditRecord.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("credit_record")
public class CreditRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("score_change")
    private Integer scoreChange; // 分数变化
    
    private String reason; // 变更原因
    
    @TableField("reason_type")
    private String reasonType; // 原因类型
    
    @TableField("related_id")
    private Long relatedId; // 关联业务ID
    
    @TableField("before_score")
    private Integer beforeScore; // 变更前分数
    
    @TableField("after_score")
    private Integer afterScore; // 变更后分数
    
    @TableField("create_time")
    private LocalDateTime createTime;
}










