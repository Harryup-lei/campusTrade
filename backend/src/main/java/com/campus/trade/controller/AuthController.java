package com.campus.trade.controller;




import com.campus.trade.common.Result;
import com.campus.trade.entity.User;
import com.campus.trade.service.IUserService;
import com.campus.trade.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/AuthController.java
 * hangu: 0 行 | kelei: 38 行 | 本文件合计: 38 行
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private com.campus.trade.service.ICreditService creditService;
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }
        
        if (userService.existsByUsername(user.getUsername())) {
            return Result.error("用户名已存在");
        }
        
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            if (userService.existsByEmail(user.getEmail())) {
                return Result.error("邮箱已被注册");
            }
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setStatus(1);
        
        // 设置默认头像
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        }
        
        userService.save(user);
        
        return Result.success("注册成功");
    }
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            
            String token = jwtUtil.generateToken(username);
            User user = userService.findByUsername(username);
            
            // 每日登录加分（检查今日是否已登录过）
            try {
                if (!creditService.hasTodayRecord(user.getId(), "每日登录")) {
                    creditService.addCreditScore(
                        user.getId(), 
                        2, 
                        "每日登录", 
                        "TASK", 
                        null
                    );
                }
            } catch (Exception e) {
                System.err.println("登录加分失败: " + e.getMessage());
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("用户名或密码错误");
        }
    }
    
    @GetMapping("/info")
    public Result<?> getUserInfo(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String username = jwtUtil.getUsernameFromToken(jwt);
        User user = userService.findByUsername(username);
        return Result.success(user);
    }
}










