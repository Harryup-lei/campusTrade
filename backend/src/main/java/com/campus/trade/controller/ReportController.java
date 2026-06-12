package com.campus.trade.controller;




import com.campus.trade.common.Result;
import com.campus.trade.entity.Report;
import com.campus.trade.entity.User;
import com.campus.trade.service.IReportService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/ReportController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/reports")
public class ReportController {
    
    @Autowired
    private IReportService reportService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * 提交举报
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<?> createReport(@RequestBody Report report, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 设置举报人ID
        report.setReporterId(user.getId());
        
        // 设置默认状态
        report.setStatus("PENDING");
        
        // 保存举报
        reportService.save(report);
        
        return Result.success("举报已提交，我们会尽快处理");
    }
}










