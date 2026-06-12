package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/News.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("news")
public class News {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    private String content;
    
    @TableField("cover_image")
    private String coverImage;
    
    @TableField("author_id")
    private Long authorId;
    
    @TableField("view_count")
    private Integer viewCount = 0;
    
    private String category; // 分类
    
    private Integer status = 1; // 1-发布 0-草稿
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    // 关联对象
    @TableField(exist = false)
    private User author;
}










