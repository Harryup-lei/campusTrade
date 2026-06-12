package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.OperationLog;
import com.campus.trade.service.IOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志Controller
 */
/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/OperationLogController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/logs")
@PreAuthorize("hasRole('ADMIN')")
public class OperationLogController {
    
    @Autowired
    private IOperationLogService operationLogService;
    
    /**
     * 分页查询操作日志
     */
    @GetMapping
    public Result<Page<OperationLog>> getLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String adminName,
            @RequestParam(required = false) String keyword
    ) {
        Page<OperationLog> pageParam = new Page<>(page, size);
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        
        // 筛选条件
        if (operationType != null && !operationType.isEmpty()) {
            wrapper.eq("operation_type", operationType);
        }
        if (module != null && !module.isEmpty()) {
            wrapper.eq("module", module);
        }
        if (adminName != null && !adminName.isEmpty()) {
            wrapper.like("admin_name", adminName);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("content", keyword);
        }
        
        // 按时间倒序
        wrapper.orderByDesc("create_time");
        
        Page<OperationLog> result = operationLogService.page(pageParam, wrapper);
        return Result.success(result);
    }
    
    /**
     * 获取日志详情
     */
    @GetMapping("/{id}")
    public Result<OperationLog> getLog(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        if (log != null) {
            return Result.success(log);
        }
        return Result.error("日志不存在");
    }
    
    /**
     * 批量删除日志（清理旧日志）
     */
    @DeleteMapping("/batch")
    public Result<?> deleteBatch(@RequestBody Long[] ids) {
        operationLogService.removeByIds(java.util.Arrays.asList(ids));
        
        // 记录删除日志的操作
        operationLogService.log("DELETE", "LOG", "批量删除操作日志，数量：" + ids.length);
        
        return Result.success("删除成功");
    }
    
    /**
     * 清空30天前的日志
     */
    @DeleteMapping("/clean")
    public Result<?> cleanOldLogs(@RequestParam(defaultValue = "30") Integer days) {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        
        if (days > 0) {
            // 清理N天前的日志
            wrapper.lt("create_time", java.time.LocalDateTime.now().minusDays(days));
        }
        // 如果days=0，不添加条件，清空所有日志
        
        long count = operationLogService.count(wrapper);
        operationLogService.remove(wrapper);
        
        // 记录清理操作
        String msg = days > 0 ? 
            "清理" + days + "天前的操作日志，数量：" + count :
            "清空所有操作日志，数量：" + count;
        operationLogService.log("DELETE", "LOG", msg);
        
        return Result.success("已清理" + count + "条日志");
    }
    
    /**
     * 清空所有日志
     */
    @DeleteMapping("/clean-all")
    public Result<?> cleanAllLogs() {
        long count = operationLogService.count();
        operationLogService.remove(new QueryWrapper<>());
        
        // 记录清理操作（这条日志也会被清理，但会保留这次操作的记录）
        operationLogService.log("DELETE", "LOG", "清空所有操作日志，数量：" + count);
        
        return Result.success("已清空" + count + "条日志");
    }
}










