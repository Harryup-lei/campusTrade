package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.News;
import com.campus.trade.entity.User;
import com.campus.trade.service.INewsService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/NewsController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/news")
public class NewsController {
    
    @Autowired
    private INewsService newsService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * 获取资讯列表（公开访问）
     */
    @GetMapping
    public Result<?> getNews(
                        @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
            System.out.println("========== 获取资讯列表 ==========");
        System.out.println("page: " + page + ", size: " + size);
        System.out.println("category: " + category);
        
        Page<News> pageParam = new Page<>(page + 1, size);
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询已发布的
        
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        IPage<News> newsPage = newsService.page(pageParam, queryWrapper);
        
        // 填充作者信息
        for (News news : newsPage.getRecords()) {
        if (news.getAuthorId() != null) {
                news.setAuthor(userService.getById(news.getAuthorId()));
            }
        }
        
        System.out.println("查询到资讯数量: " + newsPage.getRecords().size());
        System.out.println("===================================");
        
        return Result.success(newsPage);
    }
    
    /**
     * 获取资讯详情
     */
    @GetMapping("/{id}")
    public Result<?> getNewsDetail(@PathVariable Long id) {
        News news = newsService.getById(id);
        if (news == null) {
            return Result.error("资讯不存在");
        }
        
        // 增加浏览量
        news.setViewCount(news.getViewCount() + 1);
        newsService.updateById(news);
        
        // 填充作者信息
        if (news.getAuthorId() != null) {
            news.setAuthor(userService.getById(news.getAuthorId()));
        }
        
        return Result.success(news);
    }
    
    /**
     * 发布资讯（仅管理员）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> createNews(@RequestBody News news, Authentication authentication) {
        System.out.println("========== 发布资讯 ==========");
        System.out.println("标题: " + news.getTitle());
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        news.setAuthorId(currentUser.getId());
        news.setViewCount(0);
        news.setStatus(1); // 直接发布
        news.setCreateTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());
        
        newsService.save(news);
        
        System.out.println("资讯发布成功，ID: " + news.getId());
        System.out.println("==============================");
        
        return Result.success(news);
    }
    
    /**
     * 更新资讯（仅管理员）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateNews(@PathVariable Long id, @RequestBody News news) {
        News existing = newsService.getById(id);
        if (existing == null) {
            return Result.error("资讯不存在");
        }
        
        existing.setTitle(news.getTitle());
        existing.setContent(news.getContent());
        existing.setCoverImage(news.getCoverImage());
        existing.setCategory(news.getCategory());
        existing.setUpdateTime(LocalDateTime.now());
        
        newsService.updateById(existing);
        
        return Result.success(existing);
    }
    
    /**
     * 删除资讯（仅管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteNews(@PathVariable Long id) {
        newsService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 获取我的资讯（管理员）
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getMyNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Page<News> pageParam = new Page<>(page + 1, size);
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author_id", currentUser.getId());
        queryWrapper.orderByDesc("create_time");
        
        IPage<News> newsPage = newsService.page(pageParam, queryWrapper);
        
        // 填充作者信息
        for (News news : newsPage.getRecords()) {
            news.setAuthor(currentUser);
        }
        
        return Result.success(newsPage);
    }
}










