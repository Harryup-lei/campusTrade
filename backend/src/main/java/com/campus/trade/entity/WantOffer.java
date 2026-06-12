package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/WantOffer.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("want_offer")
public class WantOffer {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long wantId;
    
    private Long sellerId;
    
    private BigDecimal price;
    
    private String description;
    
    private String contact;
    
    private String location;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}










