package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.OperationLog;
import com.campus.trade.mapper.OperationLogMapper;
import com.campus.trade.service.IOperationLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 操作日志Service实现
 */
/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/OperationLogServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {
    
    @Override
    public void log(String operationType, String module, String content) {
        log(null, "admin", operationType, module, content);
    }
    
    @Override
    public void log(Long adminId, String adminName, String operationType, String module, String content) {
        OperationLog log = new OperationLog();
        log.setAdminId(adminId);
        log.setAdminName(adminName != null ? adminName : "admin");
        log.setOperationType(operationType);
        log.setModule(module);
        log.setContent(content);
        log.setIp("127.0.0.1"); // 简化处理，实际应该获取真实IP
        log.setStatus(1);
        log.setCreateTime(LocalDateTime.now());
        
        // 异步保存日志，不影响主业务
        try {
            this.save(log);
        } catch (Exception e) {
            // 日志记录失败不影响主业务
            e.printStackTrace();
        }
    }
}










