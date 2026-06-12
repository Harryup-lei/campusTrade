package com.campus.trade.entity;




import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/entity/OperationLog.java
 * hangu: 42 行 | kelei: 0 行 | 本文件合计: 42 行
 */
@Data
@TableName("operation_log")
    public class OperationLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
                                                                                                                                @TableField("admin_id")
    private Long adminId;
    
    @TableField("admin_name")
    private String adminName;
    
    @TableField("operation_type")
    private String operationType;  // CREATE UPDATE DELETE QUERY LOGIN
    
    private String module;  // USER PRODUCT ORDER COMMENT REPORT等
    
    private String content;  // 操作内容描述
    
    private String method;  // 请求方法
    
    private String params;  // 请求参数
    
    private String ip;  // IP地址
    
    private String location;  // IP归属地
    
    private String browser;  // 浏览器
    
    private String os;  // 操作系统
    
    private Integer status = 1;  // 0-失败 1-成功
    
    @TableField("error_msg")
    private String errorMsg;  // 错误信息
    
    @TableField("execute_time")
    private Long executeTime;  // 执行耗时
    
    @TableField("create_time")
    private LocalDateTime createTime;
}










