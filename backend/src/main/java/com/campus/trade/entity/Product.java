package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/Product.java
 * hangu: 48 行 | kelei: 0 行 | 本文件合计: 48 行
 */
@Data
@TableName(value = "product", autoResultMap = true)
    public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
                                                                                                                                private String description;
    private BigDecimal price;
    
    @TableField("original_price")
    private BigDecimal originalPrice;
    
    private Integer stock = 10;  // 默认库存10件
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images; // 图片URL列表，存储为JSON数组
    
    @TableField("category_id")
    private Long categoryId;
    
    @TableField(exist = false)
    private Category category; // 分类信息（关联对象）
    
    @TableField("seller_id")
    private Long sellerId;
    
    @TableField(exist = false)
    private User seller; // 卖家信息（关联对象）
    
    @TableField("condition_desc")
    private String conditionDesc; // 成色描述
    
    private String location;
    
    @TableField("view_count")
    private Integer viewCount = 0;
    
    @TableField("favorite_count")
    private Integer favoriteCount = 0;
    
    private String status = "ON_SALE"; // ON_SALE-在售 SOLD-已售 OFF_SALE-下架
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}










