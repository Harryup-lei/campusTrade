package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/INewsService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.News;

public interface INewsService extends IService<News> {
    IPage<News> findByStatus(Integer status, Page<News> page);
}










