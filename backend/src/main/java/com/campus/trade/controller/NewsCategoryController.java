package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.NewsCategory;
import com.campus.trade.service.INewsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/NewsCategoryController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/news-categories")
public class NewsCategoryController {
    
    @Autowired
    private INewsCategoryService newsCategoryService;
    
    /**
     * 获取所有启用的分类（公开访问）
     */
    @GetMapping("/enabled")
    public Result<?> getEnabledCategories() {
        QueryWrapper<NewsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询启用的
        queryWrapper.orderByAsc("sort_order"); // 按排序号排序
        
        List<NewsCategory> categories = newsCategoryService.list(queryWrapper);
        return Result.success(categories);
    }
    
    /**
     * 获取所有分类（管理员）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<NewsCategory> pageParam = new Page<>(page + 1, size);
        QueryWrapper<NewsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_order");
        
        IPage<NewsCategory> categoryPage = newsCategoryService.page(pageParam, queryWrapper);
        return Result.success(categoryPage);
    }
    
    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Result<?> getCategoryById(@PathVariable Long id) {
        NewsCategory category = newsCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }
    
    /**
     * 创建分类（管理员）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> createCategory(@RequestBody NewsCategory category) {
        // 检查分类名称是否已存在
        QueryWrapper<NewsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", category.getName());
        if (newsCategoryService.count(queryWrapper) > 0) {
            return Result.error("分类名称已存在");
        }
        
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        
        newsCategoryService.save(category);
        return Result.success(category);
    }
    
    /**
     * 更新分类（管理员）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateCategory(@PathVariable Long id, @RequestBody NewsCategory category) {
        NewsCategory existingCategory = newsCategoryService.getById(id);
        if (existingCategory == null) {
            return Result.error("分类不存在");
        }
        
        // 检查分类名称是否与其他分类重复
        QueryWrapper<NewsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", category.getName());
        queryWrapper.ne("id", id);
        if (newsCategoryService.count(queryWrapper) > 0) {
            return Result.error("分类名称已存在");
        }
        
        category.setId(id);
        newsCategoryService.updateById(category);
        return Result.success(category);
    }
    
    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteCategory(@PathVariable Long id) {
        NewsCategory category = newsCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        
        // TODO: 检查是否有资讯使用该分类，如果有则不允许删除
        
        newsCategoryService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 启用/禁用分类（管理员）
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        NewsCategory category = newsCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        
        category.setStatus(status);
        newsCategoryService.updateById(category);
        return Result.success("状态更新成功");
    }
}










