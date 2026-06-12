package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/ICommentService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Comment;

import java.util.List;

public interface ICommentService extends IService<Comment> {
    // 获取商品的所有顶级评论（包含回复）
    List<Comment> getCommentsByProductId(Long productId);
}










