package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Comment;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.User;
import com.campus.trade.service.ICommentService;
import com.campus.trade.service.IProductService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/CommentController.java
 * hangu: 51 行 | kelei: 47 行 | 本文件合计: 98 行
 */
@RestController
@RequestMapping("/comments")
public class CommentController {
    
    @Autowired
    private ICommentService commentService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private IProductService productService;
    
    @Autowired
    private com.campus.trade.service.ICreditService creditService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    
    /**
     * 获取商品的所有评论（包含回复）
     */
    @GetMapping("/product/{productId}")
    public Result<?> getProductComments(@PathVariable Long productId) {
        System.out.println("========== 获取商品评论 ==========");
        System.out.println("商品ID: " + productId);
        List<Comment> comments = commentService.getCommentsByProductId(productId);
        System.out.println("评论数量: " + comments.size());
        System.out.println("===================================");
        return Result.success(comments);
    }
    
    /**
     * 创建评论
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
        public Result<?> createComment(@RequestBody Comment comment, Authentication authentication) {
        System.out.println("========== 创建评论 ==========");
        System.out.println("商品ID: " + comment.getProductId());
        System.out.println("用户: " + authentication.getName());
        System.out.println("内容: " + comment.getContent());
                                                                                                                                                                                                // 保存评论
        commentService.save(comment);
        
        // 给商品卖家增加信用分（收到评论视为好评）
        // 规则：
        // 1. 只有主评论（非回复）才加分
        // 2. 同一用户对同一商品只能让卖家获得一次好评加分
        try {
            // 只有主评论才加分（parentId为null）
        if (comment.getParentId() == null) {
                Product product = productService.getById(comment.getProductId());
                if (product != null && product.getSellerId() != null) {
                    // 检查该用户是否已经对这个商品评论过并让卖家加过分
                    QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("product_id", comment.getProductId());
                    queryWrapper.eq("user_id", comment.getUserId());
                    queryWrapper.isNull("parent_id"); // 只查主评论
                    
                    long commentCount = commentService.count(queryWrapper);
                    
                    // 如果这是该用户对此商品的第一条主评论，才给卖家加分
                    if (commentCount == 1) {
                        creditService.addCreditScore(
                            product.getSellerId(), 
                            10, 
                            "获得好评", 
                            "REVIEW", 
                            comment.getId()
                        );
                        System.out.println("卖家获得好评 +10 信用分");
                    } else {
                        System.out.println("该用户已对此商品评论过，不重复加分");
                    }
                }
            } else {
                System.out.println("回复不加分");
            }
        } catch (Exception e) {
            System.err.println("增加信用分失败: " + e.getMessage());
        }
        
        System.out.println("评论ID: " + comment.getId());
        System.out.println("================================");
        return Result.success(comment);
    }
    
    /**
     * 创建回复
     */
    @PostMapping("/{commentId}/replies")
    @PreAuthorize("isAuthenticated()")
    public Result<?> createReply(@PathVariable Long commentId, @RequestBody Comment reply, Authentication authentication) {
        System.out.println("========== 创建回复 ==========");
        System.out.println("父评论ID: " + commentId);
        System.out.println("用户: " + authentication.getName());
        System.out.println("内容: " + reply.getContent());
        
        // 设置父评论ID
        reply.setParentId(commentId);
        
        // 获取父评论以获取商品ID
        Comment parentComment = commentService.getById(commentId);
        if (parentComment != null) {
            reply.setProductId(parentComment.getProductId());
        }
        
        // 保存回复
        commentService.save(reply);
        
        System.out.println("回复ID: " + reply.getId());
        System.out.println("===============================");
        return Result.success(reply);
    }
    
    /**
     * 删除评论（管理员或评论作者）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<?> deleteComment(@PathVariable Long id, Authentication authentication) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        
        // TODO: 检查权限（管理员或评论作者）
        
        commentService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 管理员获取所有评论（分页）
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
        public Result<?> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        System.out.println("========== 管理员获取所有评论 ==========");
                                                                                                System.out.println("page: " + page + ", size: " + size);
                System.out.println("keyword: " + keyword);
        
        Page<Comment> pageParam = new Page<>(page + 1, size);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        
        // 只查询顶级评论（不是回复）
        queryWrapper.isNull("parent_id");
        
        // 如果有关键词，搜索内容
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("content", keyword);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        IPage<Comment> commentsPage = commentService.page(pageParam, queryWrapper);
        
        // 填充用户和商品信息
        for (Comment comment : commentsPage.getRecords()) {
        if (comment.getUserId() != null) {
                comment.setUser(userService.getById(comment.getUserId()));
            }
        if (comment.getProductId() != null) {
                Product product = productService.getById(comment.getProductId());
                // 创建一个简化的Product对象，只包含id和title
                if (product != null) {
                    Product simpleProduct = new Product();
                    simpleProduct.setId(product.getId());
                    simpleProduct.setTitle(product.getTitle());
                    comment.setProduct(simpleProduct);
                }
            }
        }
        
        System.out.println("查询到评论数量: " + commentsPage.getRecords().size());
        System.out.println("=========================================");
        
        return Result.success(commentsPage);
    }
    
    /**
     * 管理员删除评论
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> adminDeleteComment(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        
        commentService.removeById(id);
        operationLogService.log("DELETE", "COMMENT", "删除评论：ID:" + id + "，内容：" + (comment.getContent().length() > 20 ? comment.getContent().substring(0, 20) + "..." : comment.getContent()));
        return Result.success("删除成功");
    }
}










