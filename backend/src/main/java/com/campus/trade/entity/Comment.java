package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/Comment.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("user_id")
    private Long userId;
    
    private String content;
    
    @TableField("parent_id")
    private Long parentId; // 父评论ID（用于回复）
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    // 关联字段（不存储在数据库中）
    @TableField(exist = false)
    private User user; // 评论用户信息
    
    @TableField(exist = false)
    private Product product; // 关联的商品信息
    
    @TableField(exist = false)
    private List<Comment> replies; // 回复列表
}










