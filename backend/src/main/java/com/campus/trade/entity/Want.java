package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/Want.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("want")
public class Want {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String title;           // 求购标题
    private String description;     // 描述
    private BigDecimal minPrice;    // 最低预算
    private BigDecimal maxPrice;    // 最高预算
    private Long userId;            // 发布人ID
    private String status;          // 状态：ACTIVE(进行中), CLOSED(已关闭)
    private String tags;            // 标签（逗号分隔）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联对象
    @TableField(exist = false)
    private User user;              // 发布人信息
}










