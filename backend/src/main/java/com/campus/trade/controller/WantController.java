package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.User;
import com.campus.trade.entity.Want;
import com.campus.trade.service.IUserService;
import com.campus.trade.service.IWantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/WantController.java
 * hangu: 53 行 | kelei: 0 行 | 本文件合计: 53 行
 */
@RestController
@RequestMapping("/wants")
public class WantController {
    
    @Autowired
    private IWantService wantService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    
    /**
     * 获取求购列表
     */
    @GetMapping
    public Result<?> getWants(
                                                                                                @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String sort) {
        
        System.out.println("========== 查询求购列表 ==========");
        System.out.println("page: " + page + ", size: " + size);
        System.out.println("keyword: " + keyword);
        System.out.println("sort: " + sort);
        
        // MyBatis Plus的Page页码从1开始
        Page<Want> pageParam = new Page<>(page + 1, size);
        
        // 构建查询条件
        QueryWrapper<Want> queryWrapper = new QueryWrapper<>();
        
        // 只查询活跃的求购
        queryWrapper.eq("status", "ACTIVE");
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("title", keyword)
                .or()
                .like("description", keyword));
        }
        
        // 排序
        if ("price_desc".equals(sort)) {
            queryWrapper.orderByDesc("max_price");
        } else {
            // 默认按最新发布排序
            queryWrapper.orderByDesc("create_time");
        }
        
        IPage<Want> wants = wantService.page(pageParam, queryWrapper);
        
        // 填充用户信息
        for (Want want : wants.getRecords()) {
        if (want.getUserId() != null) {
                want.setUser(userService.getById(want.getUserId()));
            }
        }
        
        System.out.println("查询到求购数量: " + wants.getRecords().size());
        System.out.println("总数: " + wants.getTotal());
        System.out.println("===================================");
        
        return Result.success(wants);
    }
    
    /**
     * 获取单个求购详情
     */
    @GetMapping("/{id}")
    public Result<?> getWant(@PathVariable Long id) {
        Want want = wantService.getById(id);
        if (want == null) {
            return Result.error("求购信息不存在");
        }
        
        // 填充用户信息
        if (want.getUserId() != null) {
            want.setUser(userService.getById(want.getUserId()));
        }
        
        return Result.success(want);
    }
    
    /**
     * 发布求购
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<?> createWant(@RequestBody Want want, Authentication authentication) {
        System.out.println("========== 发布求购 ==========");
        System.out.println("标题: " + want.getTitle());
        System.out.println("预算: " + want.getMinPrice() + " - " + want.getMaxPrice());
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        want.setUserId(currentUser.getId());
        want.setStatus("ACTIVE");
        want.setCreateTime(LocalDateTime.now());
        want.setUpdateTime(LocalDateTime.now());
        
        wantService.save(want);
        
        System.out.println("求购发布成功，ID: " + want.getId());
        System.out.println("================================");
        
        return Result.success(want);
    }
    
    /**
     * 更新求购
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<?> updateWant(@PathVariable Long id, @RequestBody Want want, Authentication authentication) {
        Want existing = wantService.getById(id);
        if (existing == null) {
            return Result.error("求购信息不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限
        if (!existing.getUserId().equals(currentUser.getId())) {
            return Result.error("无权限修改此求购");
        }
        
        existing.setTitle(want.getTitle());
        existing.setDescription(want.getDescription());
        existing.setMinPrice(want.getMinPrice());
        existing.setMaxPrice(want.getMaxPrice());
        existing.setTags(want.getTags());
        existing.setUpdateTime(LocalDateTime.now());
        
        wantService.updateById(existing);
        
        return Result.success(existing);
    }
    
    /**
     * 关闭求购
     */
    @PutMapping("/{id}/close")
    @PreAuthorize("isAuthenticated()")
    public Result<?> closeWant(@PathVariable Long id, Authentication authentication) {
        Want existing = wantService.getById(id);
        if (existing == null) {
            return Result.error("求购信息不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限
        if (!existing.getUserId().equals(currentUser.getId())) {
            return Result.error("无权限操作此求购");
        }
        
        existing.setStatus("CLOSED");
        existing.setUpdateTime(LocalDateTime.now());
        
        wantService.updateById(existing);
        
        return Result.success("已关闭求购");
    }
    
    /**
     * 重新开启求购
     */
    @PutMapping("/{id}/reopen")
    @PreAuthorize("isAuthenticated()")
    public Result<?> reopenWant(@PathVariable Long id, Authentication authentication) {
        Want existing = wantService.getById(id);
        if (existing == null) {
            return Result.error("求购信息不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限
        if (!existing.getUserId().equals(currentUser.getId())) {
            return Result.error("无权限操作此求购");
        }
        
        existing.setStatus("ACTIVE");
        existing.setUpdateTime(LocalDateTime.now());
        
        wantService.updateById(existing);
        
        return Result.success("已重新开启求购");
    }
    
    /**
     * 删除求购
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<?> deleteWant(@PathVariable Long id, Authentication authentication) {
        Want existing = wantService.getById(id);
        if (existing == null) {
            return Result.error("求购信息不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限：管理员或发布者可以删除
        if (!existing.getUserId().equals(currentUser.getId()) && 
            !"ADMIN".equals(currentUser.getRole())) {
            return Result.error("无权限删除此求购");
        }
        
        wantService.removeById(id);
        
        // 记录日志
        operationLogService.log("DELETE", "WANT", "删除求购：" + existing.getTitle() + "(ID:" + id + ")");
        
        return Result.success("删除成功");
    }
    
    /**
     * 获取我的求购
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Result<?> getMyWants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Page<Want> pageParam = new Page<>(page + 1, size);
        QueryWrapper<Want> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        queryWrapper.orderByDesc("create_time");
        
        IPage<Want> wants = wantService.page(pageParam, queryWrapper);
        
        // 填充用户信息
        for (Want want : wants.getRecords()) {
            want.setUser(currentUser);
        }
        
        return Result.success(wants);
    }
}










