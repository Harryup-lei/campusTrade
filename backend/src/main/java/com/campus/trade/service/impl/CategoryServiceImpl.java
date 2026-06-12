package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Category;
import com.campus.trade.mapper.CategoryMapper;
import com.campus.trade.service.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/CategoryServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    
    @Override
    public List<Category> findByStatusOrderBySortOrder(Integer status) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status).orderByAsc("sort_order");
        return this.list(wrapper);
    }
}










