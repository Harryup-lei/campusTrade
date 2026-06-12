package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/Free.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("free")
public class Free {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;            // 发布人ID
    private String title;           // 赠送标题
    private String description;     // 描述
    private Long categoryId;        // 分类ID
    private String images;          // 图片（逗号分隔）
    private String location;        // 位置
    private String contact;         // 联系方式
    private String status;          // 状态：AVAILABLE(可领取), COMPLETED(已送出), CLOSED(已关闭)
    private Integer viewCount;      // 浏览次数
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联对象
    @TableField(exist = false)
    private User user;              // 发布人信息
    
    @TableField(exist = false)
    private Category category;      // 分类信息
}










