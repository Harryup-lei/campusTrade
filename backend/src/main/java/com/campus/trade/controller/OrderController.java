package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.User;
import com.campus.trade.service.IOrderService;
import com.campus.trade.service.IProductService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/OrderController.java
 * hangu: 0 行 | kelei: 92 行 | 本文件合计: 92 行
 */
@RestController
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {
    
    @Autowired
    private IOrderService orderService;
    
    @Autowired
    private IProductService productService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    
    @Autowired
    private com.campus.trade.service.ICreditService creditService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
        public Result<?> getAllOrders(
                                                                                                @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status) {
                        System.out.println("========== 查询订单列表 ==========");
        System.out.println("page: " + page + ", size: " + size);
        System.out.println("orderNo: " + orderNo + ", status: " + status);
        
        // MyBatis Plus的Page页码从1开始，但前端传的是从0开始
        Page<Order> pageParam = new Page<>(page + 1, size);
        
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.like("order_no", orderNo);
        }
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        IPage<Order> orders = orderService.page(pageParam, queryWrapper);
        
        // 填充关联信息（buyer、seller、product）
        for (Order order : orders.getRecords()) {
        if (order.getBuyerId() != null) {
                order.setBuyer(userService.getById(order.getBuyerId()));
            }
        if (order.getSellerId() != null) {
                order.setSeller(userService.getById(order.getSellerId()));
            }
        if (order.getProductId() != null) {
                order.setProduct(productService.getById(order.getProductId()));
            }
        }
        
        System.out.println("查询到订单数量: " + orders.getRecords().size());
        System.out.println("总订单数: " + orders.getTotal());
        System.out.println("========================================");
        
        return Result.success(orders);
    }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
        public Result<?> createOrder(@RequestBody Order order, Authentication authentication) {
        System.out.println("========== 创建订单 ==========");
        System.out.println("商品ID: " + order.getProductId());
        System.out.println("数量: " + order.getQuantity());
        System.out.println("当前用户: " + authentication.getName());
        
                                                                                                // 获取当前用户ID作为买家ID
                String username = authentication.getName();
        User buyer = userService.findByUsername(username);
        if (buyer == null) {
            return Result.error("用户不存在");
        }
        order.setBuyerId(buyer.getId());
        
        // 获取商品信息
        Product product = productService.getById(order.getProductId());
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        // 设置卖家ID
        order.setSellerId(product.getSellerId());
        
        // 计算总价
        order.setTotalAmount(product.getPrice().multiply(new java.math.BigDecimal(order.getQuantity())));
        
        // 生成订单号：格式为 ORD + yyyyMMddHHmmss + 4位随机数
        // 例如：ORD + 20231203210530 + 1234 = ORD202312032105301234
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomNum = String.format("%04d", new java.util.Random().nextInt(10000));
        order.setOrderNo("ORD" + timestamp + randomNum);
        order.setStatus("PENDING");
        
        System.out.println("买家ID: " + order.getBuyerId());
        System.out.println("卖家ID: " + order.getSellerId());
        System.out.println("总价: " + order.getTotalAmount());
        System.out.println("订单号: " + order.getOrderNo());
        
        orderService.save(order);
        
        System.out.println("订单创建成功，ID: " + order.getId());
        System.out.println("================================");
        
        return Result.success(order);
    }
    
    @GetMapping("/buyer")
    public Result<?> getBuyerOrders(
            @RequestParam Long buyerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        System.out.println("========== 查询买家订单 ==========");
        System.out.println("buyerId: " + buyerId);
        
        Page<Order> pageParam = new Page<>(page + 1, size);
        IPage<Order> orders = orderService.findByBuyerId(buyerId, pageParam);
        
        // 填充关联信息
        for (Order order : orders.getRecords()) {
        if (order.getBuyerId() != null) {
                order.setBuyer(userService.getById(order.getBuyerId()));
            }
        if (order.getSellerId() != null) {
                order.setSeller(userService.getById(order.getSellerId()));
            }
        if (order.getProductId() != null) {
                order.setProduct(productService.getById(order.getProductId()));
            }
        }
        
        System.out.println("订单数量: " + orders.getRecords().size());
        System.out.println("===================================");
        
        return Result.success(orders);
    }
    
    @GetMapping("/seller")
    public Result<?> getSellerOrders(
            @RequestParam Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        System.out.println("========== 查询卖家订单 ==========");
        System.out.println("sellerId: " + sellerId);
        
        Page<Order> pageParam = new Page<>(page + 1, size);
        IPage<Order> orders = orderService.findBySellerId(sellerId, pageParam);
        
        // 填充关联信息
        for (Order order : orders.getRecords()) {
        if (order.getBuyerId() != null) {
                order.setBuyer(userService.getById(order.getBuyerId()));
            }
        if (order.getSellerId() != null) {
                order.setSeller(userService.getById(order.getSellerId()));
            }
        if (order.getProductId() != null) {
                order.setProduct(productService.getById(order.getProductId()));
            }
        }
        
        System.out.println("订单数量: " + orders.getRecords().size());
        System.out.println("===================================");
        
        return Result.success(orders);
    }
    
    @PutMapping("/{id}/pay")
    public Result<?> payOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            order.setStatus("PAID");
            order.setPayTime(LocalDateTime.now());
            orderService.updateById(order);
            
            // 记录日志
            operationLogService.log("UPDATE", "ORDER", "订单支付：订单#" + order.getOrderNo());
            
            // 更新商品库存
            Product product = productService.getById(order.getProductId());
        if (product != null) {
                product.setStock(product.getStock() - order.getQuantity());
                if (product.getStock() <= 0) {
                    product.setStatus("SOLD");
                }
                productService.updateById(product);
            }
            
            return Result.success("支付成功");
        }
        return Result.error("订单不存在");
    }
    
    @PutMapping("/{id}/ship")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public Result<?> shipOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            order.setStatus("SHIPPED");
            order.setShipTime(LocalDateTime.now());
            orderService.updateById(order);
            
            // 记录日志
            operationLogService.log("UPDATE", "ORDER", "订单发货：订单#" + order.getOrderNo());
            
            return Result.success("发货成功");
        }
        return Result.error("订单不存在");
    }
    
    @PutMapping("/{id}/complete")
    public Result<?> completeOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            order.setStatus("COMPLETED");
            order.setCompleteTime(LocalDateTime.now());
            orderService.updateById(order);
            
            // 记录日志
            operationLogService.log("UPDATE", "ORDER", "订单完成：订单#" + order.getOrderNo());
            
            // 完成交易，买卖双方加信用分
            try {
                Product product = productService.getById(order.getProductId());
                if (product != null) {
                    creditService.addCreditScore(
                        order.getBuyerId(), 
                        5, 
                        "完成交易", 
                        "TRADE", 
                        order.getId()
                    );
                    creditService.addCreditScore(
                        product.getSellerId(), 
                        10, 
                        "完成交易", 
                        "TRADE", 
                        order.getId()
                    );
                }
            } catch (Exception e) {
                System.err.println("交易完成加分失败: " + e.getMessage());
            }
            
            return Result.success("订单已完成");
        }
        return Result.error("订单不存在");
    }
    
    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            order.setStatus("CANCELLED");
            orderService.updateById(order);
            
            // 记录日志
            operationLogService.log("UPDATE", "ORDER", "取消订单：订单#" + order.getOrderNo());
            
            // 取消订单扣分（如果已支付）
        if ("PAID".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus())) {
                try {
                    creditService.addCreditScore(
                        order.getBuyerId(), 
                        -5, 
                        "取消订单", 
                        "VIOLATION", 
                        order.getId()
                    );
                } catch (Exception e) {
                    System.err.println("取消订单扣分失败: " + e.getMessage());
                }
            }
            
            return Result.success("订单已取消");
        }
        return Result.error("订单不存在");
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        Order existing = orderService.getById(id);
        if (existing != null) {
            // 只允许更新部分字段
        if (order.getQuantity() != null) {
                existing.setQuantity(order.getQuantity());
            }
        if (order.getShippingAddress() != null) {
                existing.setShippingAddress(order.getShippingAddress());
            }
        if (order.getContactPhone() != null) {
                existing.setContactPhone(order.getContactPhone());
            }
        if (order.getRemark() != null) {
                existing.setRemark(order.getRemark());
            }
            
            // 如果修改了数量，重新计算总金额
        if (order.getQuantity() != null && existing.getProductId() != null) {
                Product product = productService.getById(existing.getProductId());
                if (product != null) {
                    existing.setTotalAmount(product.getPrice().multiply(new java.math.BigDecimal(existing.getQuantity())));
                }
            }
            
            orderService.updateById(existing);
            return Result.success(existing);
        }
        return Result.error("订单不存在");
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            // 只允许删除已完成或已取消的订单
        if ("COMPLETED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
                orderService.removeById(id);
                
                // 记录日志
                operationLogService.log("DELETE", "ORDER", "删除订单：订单#" + order.getOrderNo() + "(ID:" + id + ")");
                
                return Result.success("订单删除成功");
            } else {
                return Result.error("只能删除已完成或已取消的订单");
            }
        }
        return Result.error("订单不存在");
    }
}










