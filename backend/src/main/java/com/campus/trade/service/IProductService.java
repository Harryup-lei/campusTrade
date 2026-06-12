package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/IProductService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Product;

public interface IProductService extends IService<Product> {
    IPage<Product> findByStatus(String status, Page<Product> page);
    IPage<Product> findByStatusAndCategoryId(String status, Long categoryId, Page<Product> page);
    IPage<Product> findByStatusAndTitleContaining(String status, String title, Page<Product> page);
    IPage<Product> findBySellerId(Long sellerId, Page<Product> page);
}










