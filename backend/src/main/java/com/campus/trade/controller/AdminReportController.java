package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.Report;
import com.campus.trade.entity.User;
import com.campus.trade.service.IProductService;
import com.campus.trade.service.IReportService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/AdminReportController.java
 * hangu: 0 行 | kelei: 42 行 | 本文件合计: 42 行
 */
@RestController
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {
    
    @Autowired
    private IReportService reportService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private IProductService productService;
    
    @Autowired
    private com.campus.trade.service.INotificationService notificationService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    
    /**
     * 获取举报列表（分页）
     */
    @GetMapping
    public Result<?> getReports(
                                                                                                @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
                        Page<Report> pageParam = new Page<>(page + 1, size);
        QueryWrapper<Report> queryWrapper = new QueryWrapper<>();
        
        // 状态筛选
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        IPage<Report> reportsPage = reportService.page(pageParam, queryWrapper);
        
        // 填充关联信息
        for (Report report : reportsPage.getRecords()) {
            // 填充举报人信息
            User reporter = userService.getById(report.getReporterId());
            report.setReporter(reporter);
            
            // 填充商品信息
            Product product = productService.getById(report.getProductId());
        if (product != null) {
                // 填充商品的卖家信息
                User seller = userService.getById(product.getSellerId());
                product.setSeller(seller);
            }
            report.setProduct(product);
            
            // 填充处理人信息
        if (report.getHandlerId() != null) {
                User handler = userService.getById(report.getHandlerId());
                report.setHandler(handler);
            }
        }
        
        return Result.success(reportsPage);
    }
    
    /**
     * 处理举报（受理）
     */
    @PostMapping("/{id}/accept")
    public Result<?> acceptReport(
            @PathVariable Long id,
            @RequestBody Map<String, String> params,
            Authentication authentication) {
        
        Report report = reportService.getById(id);
        if (report == null) {
            return Result.error("举报不存在");
        }
        
        String username = authentication.getName();
        User handler = userService.findByUsername(username);
        
        report.setStatus("ACCEPTED");
        report.setHandlerId(handler.getId());
        report.setHandleTime(LocalDateTime.now());
        report.setHandleResult(params.get("result"));
        
        reportService.updateById(report);
        
        // 记录日志
        operationLogService.log("UPDATE", "REPORT", "受理举报：举报#" + id);
        
        // 发送通知
        Product product = productService.getById(report.getProductId());
        if (product != null) {
            notificationService.sendReportNotification(
                report.getReporterId(),
                product.getSellerId(),
                "ACCEPTED",
                params.get("result"),
                product.getTitle()
            );
        }
        
        return Result.success("已受理举报");
    }
    
    /**
     * 处理举报（驳回）
     */
    @PostMapping("/{id}/reject")
    public Result<?> rejectReport(
            @PathVariable Long id,
            @RequestBody Map<String, String> params,
            Authentication authentication) {
        
        Report report = reportService.getById(id);
        if (report == null) {
            return Result.error("举报不存在");
        }
        
        String username = authentication.getName();
        User handler = userService.findByUsername(username);
        
        report.setStatus("REJECTED");
        report.setHandlerId(handler.getId());
        report.setHandleTime(LocalDateTime.now());
        report.setHandleResult(params.get("result"));
        
        reportService.updateById(report);
        
        // 记录日志
        operationLogService.log("UPDATE", "REPORT", "驳回举报：举报#" + id);
        
        // 发送通知（只通知举报人）
        Product product = productService.getById(report.getProductId());
        if (product != null) {
            notificationService.sendReportNotification(
                report.getReporterId(),
                null, // 驳回时不通知卖家
                "REJECTED",
                params.get("result"),
                product.getTitle()
            );
        }
        
        return Result.success("已驳回举报");
    }
    
    /**
     * 处理举报（解决）
     */
    @PostMapping("/{id}/resolve")
    public Result<?> resolveReport(
            @PathVariable Long id,
            @RequestBody Map<String, String> params,
            Authentication authentication) {
        
        Report report = reportService.getById(id);
        if (report == null) {
            return Result.error("举报不存在");
        }
        
        String username = authentication.getName();
        User handler = userService.findByUsername(username);
        
        report.setStatus("RESOLVED");
        report.setHandlerId(handler.getId());
        report.setHandleTime(LocalDateTime.now());
        
        String resultMessage = params.get("result");
        boolean shouldOffline = "true".equals(params.get("offlineProduct"));
        
        // 如果需要下架商品
        if (shouldOffline) {
            Product product = productService.getById(report.getProductId());
        if (product != null) {
                product.setStatus("OFF_SALE");
                productService.updateById(product);
                resultMessage = resultMessage + "（商品已下架）";
            }
        }
        
        report.setHandleResult(resultMessage);
        reportService.updateById(report);
        
        // 记录日志
        operationLogService.log("UPDATE", "REPORT", "处理举报：举报#" + id + (shouldOffline ? "，并下架商品" : ""));
        
        // 发送通知
        Product product = productService.getById(report.getProductId());
        if (product != null) {
            notificationService.sendReportNotification(
                report.getReporterId(),
                product.getSellerId(),
                "RESOLVED",
                resultMessage,
                product.getTitle()
            );
        }
        
        return Result.success("举报已处理完成" + (shouldOffline ? "，商品已下架" : ""));
    }
    
    /**
     * 删除举报
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteReport(@PathVariable Long id) {
        reportService.removeById(id);
        operationLogService.log("DELETE", "REPORT", "删除举报：举报#" + id);
        return Result.success("删除成功");
    }
}










