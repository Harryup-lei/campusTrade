package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.User;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/UserController.java
 * hangu: 50 行 | kelei: 50 行 | 本文件合计: 100 行
 */
@RestController
@RequestMapping("/users")
@PreAuthorize("isAuthenticated()")
public class UserController {
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private com.campus.trade.service.ICreditService creditService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    @PutMapping("/info")
    public Result<?> updateUserInfo(@RequestBody User user) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        
                                                                                                                                                                                                if (principal instanceof org.springframework.security.core.userdetails.User) {
            username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        User existing = userService.findByUsername(username);
        if (existing != null) {
            // 检查是否之前资料已完善
            boolean wasCompleted = existing.getProfileCompleted() != null && existing.getProfileCompleted();
            
            existing.setNickname(user.getNickname());
            existing.setEmail(user.getEmail());
            existing.setPhone(user.getPhone());
            existing.setAvatar(user.getAvatar());
            
            // 检查资料是否完善（有昵称、邮箱、手机、头像）
            boolean isComplete = existing.getNickname() != null && !existing.getNickname().isEmpty()
                && existing.getEmail() != null && !existing.getEmail().isEmpty()
                && existing.getPhone() != null && !existing.getPhone().isEmpty()
                && existing.getAvatar() != null && !existing.getAvatar().isEmpty();
            
            existing.setProfileCompleted(isComplete);
            userService.updateById(existing);
            
            // 如果资料从未完善到完善，给予奖励
        if (!wasCompleted && isComplete) {
                try {
                    creditService.addCreditScore(
                        existing.getId(), 
                        10, 
                        "完善资料", 
                        "TASK", 
                        null
                    );
                } catch (Exception e) {
                    System.err.println("完善资料加分失败: " + e.getMessage());
                }
            }
            
            return Result.success(existing);
        }
        return Result.error("用户不存在");
    }
    
    /**
     * 实名认证（简化版）
     */
    @PostMapping("/verify")
    public Result<?> verifyIdentity(@RequestBody Map<String, String> params) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        
        if (principal instanceof org.springframework.security.core.userdetails.User) {
                                                                                                                                                                                                username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            } else {
            username = principal.toString();
        }
        
        String studentId = params.get("studentId");
        if (studentId == null || studentId.isEmpty()) {
            return Result.error("学号不能为空");
        }
        
        // 简化验证：学号格式为10位数字，以2开头
        if (!studentId.matches("^2\\d{9}$")) {
            return Result.error("学号格式不正确，应为10位数字且以2开头");
        }
        
        User existing = userService.findByUsername(username);
        if (existing != null) {
            // 检查是否已经认证过
        if (existing.getIsVerified() != null && existing.getIsVerified()) {
                return Result.error("您已经完成认证，无需重复认证");
            }
            
            existing.setStudentId(studentId);
            existing.setIsVerified(true);
            userService.updateById(existing);
            
            // 认证成功，增加信用分
            try {
                creditService.addCreditScore(
                    existing.getId(), 
                    20, 
                    "实名认证", 
                    "TASK", 
                    null
                );
            } catch (Exception e) {
                System.err.println("认证加分失败: " + e.getMessage());
            }
            
            return Result.success("认证成功，获得20信用分");
        }
        return Result.error("用户不存在");
    }

    @PutMapping("/password")
    public Result<?> updateMyPassword(@RequestBody Map<String, String> params) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        User existing = userService.findByUsername(username);
        
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (existing != null) {
        if (!passwordEncoder.matches(oldPassword, existing.getPassword())) {
                return Result.error("原密码错误");
            }
            existing.setPassword(passwordEncoder.encode(newPassword));
            userService.updateById(existing);
            return Result.success("密码修改成功");
        }
        return Result.error("用户不存在");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // MyBatis Plus的Page页码从1开始
        Page<User> pageParam = new Page<>(page + 1, size);
        IPage<User> users = userService.page(pageParam);
        return Result.success(users);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> createUser(@RequestBody User user) {
        // 检查用户名是否已存在
        User existing = userService.findByUsername(user.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认正常状态
        }
        
        // 保存用户
        userService.save(user);
        
        // 记录日志
        operationLogService.log("CREATE", "USER", "创建用户：" + user.getUsername());
        
        return Result.success(user);
    }
    
    @GetMapping("/{id}")
    public Result<?> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }
    
    @PutMapping("/{id}")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existing = userService.getById(id);
        if (existing != null) {
            existing.setNickname(user.getNickname());
            existing.setEmail(user.getEmail());
            existing.setPhone(user.getPhone());
            existing.setAvatar(user.getAvatar());
            userService.updateById(existing);
            return Result.success(existing);
        }
        return Result.error("用户不存在");
    }
    
    @PutMapping("/{id}/password")
    public Result<?> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        User existing = userService.getById(id);
        if (existing != null) {
            existing.setPassword(passwordEncoder.encode(newPassword));
            userService.updateById(existing);
            return Result.success("密码修改成功");
        }
        return Result.error("用户不存在");
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User existing = userService.getById(id);
        if (existing != null) {
            existing.setStatus(status);
            userService.updateById(existing);
            
            // 记录日志
            String statusText = status == 1 ? "启用" : "禁用";
            operationLogService.log("UPDATE", "USER", statusText + "用户：" + existing.getUsername());
            
            return Result.success("状态更新成功");
        }
        return Result.error("用户不存在");
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        userService.removeById(id);
        
        // 记录日志
        if (user != null) {
            operationLogService.log("DELETE", "USER", "删除用户：" + user.getUsername() + "(ID:" + id + ")");
        }
        
        return Result.success("删除成功");
    }
    
    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats")
    public Result<?> getUserStats() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 获取统计数据
        Map<String, Object> stats = userService.getUserStats(user.getId());
        
        return Result.success(stats);
    }
}










