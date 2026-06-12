package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.User;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.Order;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/UserServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return this.getOne(wrapper);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return this.count(wrapper) > 0;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return this.count(wrapper) > 0;
    }
    
    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计发布的商品数量
        QueryWrapper<Product> productWrapper = new QueryWrapper<>();
        productWrapper.eq("seller_id", userId);
        long publishCount = productMapper.selectCount(productWrapper);
        
        // 统计卖出的商品数量（已完成的订单，作为卖家）
        QueryWrapper<Order> sellerOrderWrapper = new QueryWrapper<>();
        sellerOrderWrapper.eq("seller_id", userId)
                         .eq("status", "COMPLETED");
        long soldCount = orderMapper.selectCount(sellerOrderWrapper);
        
        // 统计买入的商品数量（已完成的订单，作为买家）
        QueryWrapper<Order> buyerOrderWrapper = new QueryWrapper<>();
        buyerOrderWrapper.eq("buyer_id", userId)
                        .eq("status", "COMPLETED");
        long boughtCount = orderMapper.selectCount(buyerOrderWrapper);
        
        stats.put("publishCount", publishCount);
        stats.put("soldCount", soldCount);
        stats.put("boughtCount", boughtCount);
        
        return stats;
    }
}










