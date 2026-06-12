package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/IOperationLogService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.OperationLog;

/**
 * 操作日志Service接口
 */
public interface IOperationLogService extends IService<OperationLog> {
    
    /**
     * 记录操作日志
     */
    void log(String operationType, String module, String content);
    
    /**
     * 记录操作日志（带管理员信息）
     */
    void log(Long adminId, String adminName, String operationType, String module, String content);
}










