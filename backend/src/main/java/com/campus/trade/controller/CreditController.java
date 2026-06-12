package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.common.Result;
import com.campus.trade.entity.CreditRecord;
import com.campus.trade.entity.UserCredit;
import com.campus.trade.entity.User;
import com.campus.trade.service.ICreditService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/CreditController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/credit")
public class CreditController {
    
    @Autowired
    private ICreditService creditService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * 获取当前用户的信用信息
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Result<?> getMyCredit(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Long userId = user.getId();
        
        UserCredit userCredit = creditService.getUserCredit(userId);
        
        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("userCredit", userCredit);
        data.put("levelInfo", getLevelInfo(userCredit.getLevel()));
        data.put("nextLevelScore", getNextLevelScore(userCredit.getCreditScore()));
        
        // 检查今日是否已签到
        boolean hasCheckedIn = creditService.hasTodayRecord(userId, "每日签到");
        data.put("hasCheckedIn", hasCheckedIn);
        
        return Result.success(data);
    }
    
    /**
     * 获取信用记录（分页）
     */
    @GetMapping("/records")
    @PreAuthorize("isAuthenticated()")
    public Result<?> getCreditRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Long userId = user.getId();
        
        IPage<CreditRecord> records = creditService.getCreditRecords(userId, page, size);
        return Result.success(records);
    }
    
    /**
     * 获取信用规则说明
     */
    @GetMapping("/rules")
    public Result<?> getCreditRules() {
        Map<String, Object> rules = new HashMap<>();
        
        // 信用等级规则
        rules.put("levels", new Object[]{
            Map.of("name", "优秀", "minScore", 150, "color", "#67c23a", "benefits", new String[]{"优先展示商品", "更高的曝光率", "专属标识"}),
            Map.of("name", "良好", "minScore", 100, "color", "#409eff", "benefits", new String[]{"正常交易权限", "标准服务"}),
            Map.of("name", "一般", "minScore", 60, "color", "#e6a23c", "benefits", new String[]{"部分功能受限", "需改善信用"}),
            Map.of("name", "较差", "minScore", 0, "color", "#f56c6c", "benefits", new String[]{"交易受限", "需重建信用"})
        });
        
        // 加分规则
        rules.put("earnRules", new Object[]{
            Map.of("action", "完成交易", "score", 10, "desc", "每完成一笔交易"),
            Map.of("action", "获得好评", "score", 10, "desc", "收到买家首次评论（同一买家对同一商品只加分一次）"),
            Map.of("action", "发布商品", "score", 5, "desc", "发布优质商品"),
            Map.of("action", "每日登录", "score", 2, "desc", "每天首次登录"),
            Map.of("action", "实名认证", "score", 20, "desc", "完成实名认证"),
            Map.of("action", "完善资料", "score", 10, "desc", "完善个人资料")
        });
        
        // 扣分规则
        rules.put("deductRules", new Object[]{
            Map.of("action", "取消订单", "score", -5, "desc", "无故取消已确认订单"),
            Map.of("action", "收到投诉", "score", -10, "desc", "收到有效投诉"),
            Map.of("action", "虚假描述", "score", -20, "desc", "商品描述严重不符"),
            Map.of("action", "恶意差评", "score", -15, "desc", "恶意给他人差评"),
            Map.of("action", "违规行为", "score", -30, "desc", "严重违规行为")
        });
        
        return Result.success(rules);
    }
    
    /**
     * 获取指定用户的信用信息（公开）
     */
    @GetMapping("/user/{userId}")
    public Result<?> getUserCredit(@PathVariable Long userId) {
        UserCredit userCredit = creditService.getUserCredit(userId);
        return Result.success(userCredit);
    }
    
    /**
     * 每日签到（测试接口）
     */
    @PostMapping("/daily-checkin")
    @PreAuthorize("isAuthenticated()")
    public Result<?> dailyCheckin(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Long userId = user.getId();
        
        // 检查今天是否已签到
        if (creditService.hasTodayRecord(userId, "每日签到")) {
            return Result.error("今日已签到，请明天再来");
        }
        
        creditService.addCreditScore(userId, 2, "每日签到", "TASK", null);
        return Result.success("签到成功，获得2积分");
    }
    
    // 辅助方法：获取等级信息
    private Map<String, Object> getLevelInfo(String level) {
        Map<String, Object> info = new HashMap<>();
        switch (level) {
            case "EXCELLENT":
                info.put("name", "优秀");
                info.put("color", "#67c23a");
                info.put("icon", "⭐⭐⭐⭐⭐");
                break;
            case "GOOD":
                info.put("name", "良好");
                info.put("color", "#409eff");
                info.put("icon", "⭐⭐⭐⭐");
                break;
            case "NORMAL":
                info.put("name", "一般");
                info.put("color", "#e6a23c");
                info.put("icon", "⭐⭐⭐");
                break;
            case "BAD":
                info.put("name", "较差");
                info.put("color", "#f56c6c");
                info.put("icon", "⭐⭐");
                break;
            default:
                info.put("name", "未知");
                info.put("color", "#909399");
                info.put("icon", "⭐");
        }
        return info;
    }
    
    // 辅助方法：获取下一等级所需分数
    private Integer getNextLevelScore(Integer currentScore) {
        if (currentScore < 60) return 60;
        if (currentScore < 100) return 100;
        if (currentScore < 150) return 150;
        return null; // 已是最高等级
    }
}










