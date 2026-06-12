package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Free;
import com.campus.trade.service.IFreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员 - 赠送管理
 */
/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/AdminFreeController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/admin/free")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFreeController {
    
    @Autowired
    private IFreeService freeService;
    
    /**
     * 分页查询赠送列表
     */
    @GetMapping
    public Result<Page<Free>> getFreeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        
        Page<Free> pageParam = new Page<>(page + 1, size);
        LambdaQueryWrapper<Free> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Free::getTitle, keyword)
                   .or()
                   .like(Free::getDescription, keyword);
        }
        
        // 状态筛选
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(Free::getStatus, status);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Free::getCreateTime);
        
        Page<Free> result = freeService.page(pageParam, wrapper);
        return Result.success(result);
    }
    
    /**
     * 获取赠送详情
     */
    @GetMapping("/{id}")
    public Result<Free> getFreeById(@PathVariable Long id) {
        Free free = freeService.getById(id);
        if (free == null) {
            return Result.error("赠送不存在");
        }
        return Result.success(free);
    }
    
    /**
     * 删除赠送（管理员权限）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteFree(@PathVariable Long id) {
        boolean success = freeService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    /**
     * 更新赠送状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        Free free = freeService.getById(id);
        if (free == null) {
            return Result.error("赠送不存在");
        }
        
        free.setStatus(status);
        boolean success = freeService.updateById(free);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
}










