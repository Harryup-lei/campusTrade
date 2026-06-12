package com.campus.trade.controller;




import com.campus.trade.common.Result;
import com.campus.trade.entity.Category;
import com.campus.trade.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/CategoryController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private ICategoryService categoryService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    
    @GetMapping
    public Result<?> getCategories() {
        List<Category> categories = categoryService.findByStatusOrderBySortOrder(1);
        return Result.success(categories);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getAllCategories() {
        List<Category> categories = categoryService.list();
        return Result.success(categories);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> createCategory(@RequestBody Category category) {
        categoryService.save(category);
        operationLogService.log("CREATE", "CATEGORY", "新增分类：" + category.getName());
        return Result.success(category);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateById(category);
        operationLogService.log("UPDATE", "CATEGORY", "更新分类：" + category.getName());
        return Result.success(category);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        categoryService.removeById(id);
        if (category != null) {
            operationLogService.log("DELETE", "CATEGORY", "删除分类：" + category.getName());
        }
        return Result.success("删除成功");
    }
}










