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
 * 文件: backend/src/main/java/com/campus/trade/entity/Order.java
 * hangu: 55 行 | kelei: 0 行 | 本文件合计: 55 行
 */
@Data
@TableName("`order`")
    public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
                                @TableField("order_no")
                private String orderNo;
    
    @TableField("buyer_id")
    private Long buyerId;
    
    @TableField(exist = false)
    private User buyer; // 买家信息（关联对象）
    
    @TableField("seller_id")
    private Long sellerId;
    
    @TableField(exist = false)
    private User seller; // 卖家信息（关联对象）
    
    @TableField("product_id")
    private Long productId;
    
    @TableField(exist = false)
    private Product product; // 商品信息（关联对象）
    
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    private Integer quantity = 1;
    private String status; // PENDING-待支付 PAID-已支付 SHIPPED-已发货 COMPLETED-已完成 CANCELLED-已取消
    
    @TableField("shipping_address")
    private String shippingAddress;
    
    @TableField("contact_phone")
    private String contactPhone;
    
    private String remark;
    
    @TableField("pay_time")
    private LocalDateTime payTime;
    
    @TableField("ship_time")
    private LocalDateTime shipTime;
    
    @TableField("complete_time")
    private LocalDateTime completeTime;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}










