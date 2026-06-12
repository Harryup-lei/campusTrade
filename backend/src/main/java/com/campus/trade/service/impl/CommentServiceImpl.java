package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Comment;
import com.campus.trade.mapper.CommentMapper;
import com.campus.trade.service.ICommentService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/CommentServiceImpl.java
 * hangu: 45 行 | kelei: 0 行 | 本文件合计: 45 行
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    
    @Autowired
    private IUserService userService;
        @Override
    public List<Comment> getCommentsByProductId(Long productId) {
        // 获取该商品的所有评论（包括顶级评论和回复）
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId)
                                                                                                                                                                                                                                                                                                                .orderByAsc("create_time"); // 按时间升序，这样旧的在前
        List<Comment> allComments = this.list(wrapper);
        
        // 分离顶级评论和回复
        List<Comment> topComments = allComments.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toList());
        
        List<Comment> replies = allComments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.toList());
        
        // 为每个顶级评论填充用户信息和回复
        for (Comment topComment : topComments) {
            // 填充用户信息
        if (topComment.getUserId() != null) {
                topComment.setUser(userService.getById(topComment.getUserId()));
            }
            
            // 查找该评论的所有回复
            List<Comment> commentReplies = replies.stream()
                    .filter(r -> r.getParentId().equals(topComment.getId()))
                    .collect(Collectors.toList());
            
            // 为每个回复填充用户信息
            for (Comment reply : commentReplies) {
                if (reply.getUserId() != null) {
                    reply.setUser(userService.getById(reply.getUserId()));
                }
            }
            
            topComment.setReplies(commentReplies);
        }
        
        // 按创建时间倒序排列（最新的在前）
        topComments.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        
        return topComments;
    }
}










