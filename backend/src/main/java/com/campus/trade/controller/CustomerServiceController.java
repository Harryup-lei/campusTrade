package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.CustomerService;
import com.campus.trade.entity.User;
import com.campus.trade.service.ICustomerServiceService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 客服咨询Controller
 */
/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/CustomerServiceController.java
 * hangu: 0 行 | kelei: 76 行 | 本文件合计: 76 行
 */
@RestController
@RequestMapping("/customer-service")
public class CustomerServiceController {
    
    @Autowired
    private ICustomerServiceService customerServiceService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    
    /**
     * 用户提交咨询（无需登录）
     */
    @PostMapping("/submit")
    public Result<?> submitConsultation(@RequestBody CustomerService consultation, Authentication authentication) {
        // 设置默认状态
        consultation.setStatus("PENDING");
        consultation.setCategory(consultation.getCategory() != null ? consultation.getCategory() : "GENERAL");
                                                                                                                                                                                                System.out.println("===== 提交咨询 =====");
        System.out.println("authentication: " + authentication);
        
        // 如果用户已登录，记录用户ID
        if (authentication != null && !"anonymousUser".equals(authentication.getName())) {
            try {
                String username = authentication.getName();
                System.out.println("username: " + username);
                User user = userService.findByUsername(username);
                if (user != null) {
                    consultation.setUserId(user.getId());
                    // 如果前端没有提供userName，使用数据库中的
                    if (consultation.getUserName() == null || consultation.getUserName().isEmpty()) {
                        consultation.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                    }
                    System.out.println("已关联用户ID: " + user.getId());
                }
            } catch (Exception e) {
                System.out.println("获取用户信息失败: " + e.getMessage());
                // 继续执行，不影响匿名提交
            }
        } else {
            System.out.println("匿名提交咨询");
        }
        
        customerServiceService.save(consultation);
        System.out.println("咨询已保存，ID: " + consultation.getId());
        
        return Result.success("提交成功！我们会尽快处理您的咨询");
    }
    
    /**
     * 用户查询自己的咨询（需要登录）
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Result<?> getMyConsultations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Page<CustomerService> pageParam = new Page<>(page + 1, size);
        QueryWrapper<CustomerService> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId())
               .orderByDesc("create_time");
        
        IPage<CustomerService> consultations = customerServiceService.page(pageParam, wrapper);
        
        return Result.success(consultations);
    }
    
    /**
     * 用户查看咨询详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<?> getConsultationDetail(@PathVariable Long id, Authentication authentication) {
        CustomerService consultation = customerServiceService.getById(id);
        
        if (consultation == null) {
            return Result.error("咨询不存在");
        }
        
        // 验证是否是本人的咨询
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        if (!consultation.getUserId().equals(user.getId())) {
            return Result.error("无权查看此咨询");
        }
        
        return Result.success(consultation);
    }
    
    /**
     * 管理员获取咨询列表（分页）
     */
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
        public Result<?> getConsultationList(
                                                @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
                        Page<CustomerService> pageParam = new Page<>(page + 1, size);
                QueryWrapper<CustomerService> wrapper = new QueryWrapper<>();
        
        // 状态筛选
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        
        // 类型筛选
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("subject", keyword)
                           .or().like("content", keyword)
                           .or().like("user_name", keyword));
        }
        
        wrapper.orderByDesc("create_time");
        
        IPage<CustomerService> consultations = customerServiceService.page(pageParam, wrapper);
        
        // 填充用户信息
        for (CustomerService cs : consultations.getRecords()) {
        if (cs.getUserId() != null) {
                cs.setUser(userService.getById(cs.getUserId()));
            }
        }
        
        return Result.success(consultations);
    }
    
    /**
     * 管理员回复咨询
     */
    @PutMapping("/admin/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> replyConsultation(
            @PathVariable Long id,
            @RequestBody Map<String, String> params,
            Authentication authentication) {
        
        CustomerService consultation = customerServiceService.getById(id);
        if (consultation == null) {
            return Result.error("咨询不存在");
        }
        
        String username = authentication.getName();
        User admin = userService.findByUsername(username);
        
        consultation.setReply(params.get("reply"));
        consultation.setReplyTime(LocalDateTime.now());
        consultation.setAdminId(admin.getId());
        consultation.setAdminName(admin.getUsername());
        consultation.setStatus("REPLIED");
        
        customerServiceService.updateById(consultation);
        
        // 记录日志
        operationLogService.log("UPDATE", "CUSTOMER_SERVICE", "回复客服咨询：" + consultation.getSubject());
        
        return Result.success("回复成功");
    }
    
    /**
     * 管理员更新咨询状态
     */
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        
        CustomerService consultation = customerServiceService.getById(id);
        if (consultation == null) {
            return Result.error("咨询不存在");
        }
        
        consultation.setStatus(status);
        customerServiceService.updateById(consultation);
        
        // 记录日志
        String statusText = "";
        switch (status) {
            case "PROCESSING": statusText = "处理中"; break;
            case "REPLIED": statusText = "已回复"; break;
            case "CLOSED": statusText = "已关闭"; break;
        }
        operationLogService.log("UPDATE", "CUSTOMER_SERVICE", "更新咨询状态为" + statusText + "：" + consultation.getSubject());
        
        return Result.success("状态更新成功");
    }
    
    /**
     * 管理员删除咨询
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteConsultation(@PathVariable Long id) {
        CustomerService consultation = customerServiceService.getById(id);
        customerServiceService.removeById(id);
        
        // 记录日志
        if (consultation != null) {
            operationLogService.log("DELETE", "CUSTOMER_SERVICE", "删除客服咨询：" + consultation.getSubject());
        }
        
        return Result.success("删除成功");
    }
    
    /**
     * 获取咨询统计
     */
    @GetMapping("/admin/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getStatistics() {
        QueryWrapper<CustomerService> wrapper = new QueryWrapper<>();
        
        long pending = customerServiceService.count(wrapper.eq("status", "PENDING"));
        wrapper.clear();
        long processing = customerServiceService.count(wrapper.eq("status", "PROCESSING"));
        wrapper.clear();
        long replied = customerServiceService.count(wrapper.eq("status", "REPLIED"));
        wrapper.clear();
        long closed = customerServiceService.count(wrapper.eq("status", "CLOSED"));
        
        java.util.Map<String, Long> statistics = new java.util.HashMap<>();
        statistics.put("pending", pending);
        statistics.put("processing", processing);
        statistics.put("replied", replied);
        statistics.put("closed", closed);
        statistics.put("total", pending + processing + replied + closed);
        
        return Result.success(statistics);
    }
}










