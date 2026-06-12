package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.News;
import com.campus.trade.mapper.NewsMapper;
import com.campus.trade.service.INewsService;
import org.springframework.stereotype.Service;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/NewsServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements INewsService {
    
    @Override
    public IPage<News> findByStatus(Integer status, Page<News> page) {
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status).orderByDesc("create_time");
        return this.page(page, wrapper);
    }
}










