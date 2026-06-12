package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/Category.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String description;
    private String icon;
    
    @TableField("sort_order")
    private Integer sortOrder = 0;
    
    private Integer status = 1; // 1-启用 0-禁用
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}










