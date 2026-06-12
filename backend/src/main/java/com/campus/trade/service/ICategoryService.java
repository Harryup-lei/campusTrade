package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/ICategoryService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Category;

import java.util.List;

public interface ICategoryService extends IService<Category> {
    List<Category> findByStatusOrderBySortOrder(Integer status);
}










