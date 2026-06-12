package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Order;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.service.IOrderService;
import org.springframework.stereotype.Service;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    
    @Override
    public Order findByOrderNo(String orderNo) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        return this.getOne(wrapper);
    }
    
    @Override
    public IPage<Order> findByBuyerId(Long buyerId, Page<Order> page) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("buyer_id", buyerId).orderByDesc("create_time");
        return this.page(page, wrapper);
    }
    
    @Override
    public IPage<Order> findBySellerId(Long sellerId, Page<Order> page) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id", sellerId).orderByDesc("create_time");
        return this.page(page, wrapper);
    }
}










