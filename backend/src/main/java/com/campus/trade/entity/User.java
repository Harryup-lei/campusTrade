package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/User.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String role; // USER, SELLER, ADMIN
    private Integer status = 1; // 1-正常 0-禁用
    
    @TableField("student_id")
    private String studentId; // 学号
    
    @TableField("is_verified")
    private Boolean isVerified = false; // 是否已实名认证
    
    @TableField("profile_completed")
    private Boolean profileCompleted = false; // 资料是否完善
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}










