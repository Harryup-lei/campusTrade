package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.CreditRecord;
import com.campus.trade.entity.User;
import com.campus.trade.entity.UserCredit;
import com.campus.trade.mapper.UserCreditMapper;
import com.campus.trade.service.ICreditService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/AdminCreditController.java
 * hangu: 0 行 | kelei: 45 行 | 本文件合计: 45 行
 */
@RestController
@RequestMapping("/admin/credits")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCreditController {
    
    @Autowired
    private ICreditService creditService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private UserCreditMapper userCreditMapper;
    
    /**
     * 获取所有用户信用列表（分页）
     */
    @GetMapping
    public Result<?> getAllCredits(
                                                                                                @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String level) {
        
        Page<UserCredit> pageParam = new Page<>(page + 1, size);
        QueryWrapper<UserCredit> queryWrapper = new QueryWrapper<>();
        
        // 根据等级筛选
        if (level != null && !level.isEmpty()) {
            queryWrapper.eq("level", level);
        }
        
        // 如果有关键词，通过用户名搜索
        if (keyword != null && !keyword.isEmpty()) {
            // 先查找用户
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.like("username", keyword)
                    .or().like("nickname", keyword);
            var users = userService.list(userQueryWrapper);
            
        if (!users.isEmpty()) {
                var userIds = users.stream().map(User::getId).toList();
                queryWrapper.in("user_id", userIds);
            } else {
                // 没有匹配的用户，返回空结果
                return Result.success(new Page<>());
            }
        }
        
        queryWrapper.orderByDesc("credit_score");
        
        IPage<UserCredit> creditsPage = userCreditMapper.selectPage(pageParam, queryWrapper);
        
        // 填充用户信息
        for (UserCredit credit : creditsPage.getRecords()) {
            User user = userService.getById(credit.getUserId());
            credit.setUser(user);
        }
        
        return Result.success(creditsPage);
    }
    
    /**
     * 获取指定用户的信用记录
     */
    @GetMapping("/{userId}/records")
    public Result<?> getUserCreditRecords(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        IPage<CreditRecord> records = creditService.getCreditRecords(userId, page + 1, size);
        return Result.success(records);
    }
    
    /**
     * 管理员增加用户信用分
     */
    @PostMapping("/{userId}/add")
    public Result<?> addCreditScore(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> params) {
        
        Integer score = (Integer) params.get("score");
        String reason = (String) params.get("reason");
        String changeType = (String) params.getOrDefault("changeType", "ADMIN");
        
        if (score == null || score <= 0) {
            return Result.error("分数必须大于0");
        }
        
        if (reason == null || reason.isEmpty()) {
            return Result.error("请输入调整原因");
        }
        
        creditService.addCreditScore(userId, score, reason, changeType, null);
        return Result.success("信用分增加成功");
    }
    
    /**
     * 管理员扣除用户信用分
     */
    @PostMapping("/{userId}/deduct")
    public Result<?> deductCreditScore(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> params) {
        
        Integer score = (Integer) params.get("score");
        String reason = (String) params.get("reason");
        String changeType = (String) params.getOrDefault("changeType", "ADMIN");
        
        if (score == null || score <= 0) {
            return Result.error("分数必须大于0");
        }
        
        if (reason == null || reason.isEmpty()) {
            return Result.error("请输入调整原因");
        }
        
        creditService.deductCreditScore(userId, score, reason, changeType, null);
        return Result.success("信用分扣除成功");
    }
    
    /**
     * 重置用户信用分
     */
    @PostMapping("/{userId}/reset")
    public Result<?> resetCreditScore(@PathVariable Long userId) {
        UserCredit userCredit = creditService.getUserCredit(userId);
        
        userCredit.setCreditScore(100);
        userCredit.setLevel("GOOD");
        userCredit.setTotalEarned(0);
        userCredit.setTotalDeducted(0);
        
        userCreditMapper.updateById(userCredit);
        
        // 记录重置操作
        creditService.addCreditScore(userId, 0, "[管理员] 重置信用分为100", "ADMIN", null);
        
        return Result.success("信用分已重置为100");
    }
    
    /**
     * 重置用户实名认证
     */
    @PostMapping("/{userId}/reset-verification")
    public Result<?> resetVerification(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setIsVerified(false);
        user.setStudentId(null);
        userService.updateById(user);
        
        return Result.success("实名认证已重置");
    }
}










