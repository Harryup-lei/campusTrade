package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Free;
import com.campus.trade.entity.User;
import com.campus.trade.service.IFreeService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/FreeController.java
 * hangu: 0 行 | kelei: 48 行 | 本文件合计: 48 行
 */
@RestController
@RequestMapping("/free")
public class FreeController {
    
    @Autowired
    private IFreeService freeService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    
    /**
     * 获取免费赠送列表
     */
    @GetMapping
    public Result<?> getFreeList(
                                                                                                @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String sort) {
        
        System.out.println("========== 查询免费赠送列表 ==========");
        System.out.println("page: " + page + ", size: " + size);
        System.out.println("keyword: " + keyword);
        System.out.println("sort: " + sort);
        
        // MyBatis Plus的Page页码从1开始
        Page<Free> pageParam = new Page<>(page + 1, size);
        
        // 构建查询条件
        QueryWrapper<Free> queryWrapper = new QueryWrapper<>();
        
        // 只查询可领取的
        queryWrapper.eq("status", "AVAILABLE");
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("title", keyword)
                .or()
                .like("description", keyword));
        }
        
        // 排序 - 默认按最新发布
        queryWrapper.orderByDesc("create_time");
        
        IPage<Free> freeItems = freeService.page(pageParam, queryWrapper);
        
        // 填充用户信息
        for (Free free : freeItems.getRecords()) {
        if (free.getUserId() != null) {
                free.setUser(userService.getById(free.getUserId()));
            }
        }
        
        System.out.println("查询到赠送数量: " + freeItems.getRecords().size());
        System.out.println("总数: " + freeItems.getTotal());
        System.out.println("===================================");
        
        return Result.success(freeItems);
    }
    
    /**
     * 获取单个赠送详情
     */
    @GetMapping("/{id}")
    public Result<?> getFreeItem(@PathVariable Long id) {
        Free free = freeService.getById(id);
        if (free == null) {
            return Result.error("赠送信息不存在");
        }
        
        // 填充用户信息
        if (free.getUserId() != null) {
            free.setUser(userService.getById(free.getUserId()));
        }
        
        return Result.success(free);
    }
    
    /**
     * 发布免费赠送
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<?> createFree(@RequestBody Free free, Authentication authentication) {
        System.out.println("========== 发布免费赠送 ==========");
        System.out.println("标题: " + free.getTitle());
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        free.setUserId(currentUser.getId());
        free.setStatus("AVAILABLE");
        free.setViewCount(0);
        free.setCreateTime(LocalDateTime.now());
        free.setUpdateTime(LocalDateTime.now());
        
        freeService.save(free);
        
        System.out.println("赠送发布成功，ID: " + free.getId());
        System.out.println("================================");
        
        return Result.success(free);
    }
    
    /**
     * 更新赠送
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<?> updateFree(@PathVariable Long id, @RequestBody Free free, Authentication authentication) {
        Free existing = freeService.getById(id);
        if (existing == null) {
            return Result.error("赠送信息不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限
        if (!existing.getUserId().equals(currentUser.getId())) {
            return Result.error("无权限修改此赠送");
        }
        
        existing.setTitle(free.getTitle());
        existing.setDescription(free.getDescription());
        existing.setCategoryId(free.getCategoryId());
        existing.setImages(free.getImages());
        existing.setLocation(free.getLocation());
        existing.setContact(free.getContact());
        existing.setUpdateTime(LocalDateTime.now());
        
        freeService.updateById(existing);
        
        return Result.success(existing);
    }
    
    /**
     * 标记为已送出
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("isAuthenticated()")
    public Result<?> completeFree(@PathVariable Long id, Authentication authentication) {
        Free existing = freeService.getById(id);
        if (existing == null) {
            return Result.error("赠送信息不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限
        if (!existing.getUserId().equals(currentUser.getId())) {
            return Result.error("无权限操作此赠送");
        }
        
        existing.setStatus("COMPLETED");
        existing.setUpdateTime(LocalDateTime.now());
        
        freeService.updateById(existing);
        
        return Result.success("已标记为送出");
    }
    
    /**
     * 重新上架
     */
    @PutMapping("/{id}/reopen")
    @PreAuthorize("isAuthenticated()")
    public Result<?> reopenFree(@PathVariable Long id, Authentication authentication) {
        Free existing = freeService.getById(id);
        if (existing == null) {
            return Result.error("赠送信息不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限
        if (!existing.getUserId().equals(currentUser.getId())) {
            return Result.error("无权限操作此赠送");
        }
        
        existing.setStatus("AVAILABLE");
        existing.setUpdateTime(LocalDateTime.now());
        
        freeService.updateById(existing);
        
        return Result.success("已重新上架");
    }
    
    /**
     * 删除赠送
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<?> deleteFree(@PathVariable Long id, Authentication authentication) {
        Free existing = freeService.getById(id);
        if (existing == null) {
            return Result.error("赠送信息不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限：管理员或发布者可以删除
        if (!existing.getUserId().equals(currentUser.getId()) && 
            !"ADMIN".equals(currentUser.getRole())) {
            return Result.error("无权限删除此赠送");
        }
        
        freeService.removeById(id);
        
        // 记录日志
        operationLogService.log("DELETE", "FREE", "删除赠送：" + existing.getTitle() + "(ID:" + id + ")");
        
        return Result.success("删除成功");
    }
    
    /**
     * 获取我的赠送
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Result<?> getMyFree(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Page<Free> pageParam = new Page<>(page + 1, size);
        QueryWrapper<Free> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        queryWrapper.orderByDesc("create_time");
        
        IPage<Free> freeItems = freeService.page(pageParam, queryWrapper);
        
        // 填充用户信息
        for (Free free : freeItems.getRecords()) {
            free.setUser(currentUser);
        }
        
        return Result.success(freeItems);
    }
}










