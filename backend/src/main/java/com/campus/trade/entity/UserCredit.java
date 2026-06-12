package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/UserCredit.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("user_credit")
public class UserCredit {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("credit_score")
    private Integer creditScore; // 当前信用分
    
    private String level; // 信用等级：EXCELLENT/GOOD/NORMAL/BAD
    
    @TableField("total_earned")
    private Integer totalEarned; // 累计获得
    
    @TableField("total_deducted")
    private Integer totalDeducted; // 累计扣除
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    // 关联用户信息（非数据库字段）
    @TableField(exist = false)
    private User user;
}










