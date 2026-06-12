package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Product;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.IProductService;
import org.springframework.stereotype.Service;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/ProductServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {
    
    @Override
    public IPage<Product> findByStatus(String status, Page<Product> page) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status).orderByDesc("create_time");
        return this.page(page, wrapper);
    }
    
    @Override
    public IPage<Product> findByStatusAndCategoryId(String status, Long categoryId, Page<Product> page) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status).eq("category_id", categoryId).orderByDesc("create_time");
        return this.page(page, wrapper);
    }
    
    @Override
    public IPage<Product> findByStatusAndTitleContaining(String status, String title, Page<Product> page) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status).like("title", title).orderByDesc("create_time");
        return this.page(page, wrapper);
    }
    
    @Override
    public IPage<Product> findBySellerId(Long sellerId, Page<Product> page) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id", sellerId).orderByDesc("create_time");
        return this.page(page, wrapper);
    }
}










