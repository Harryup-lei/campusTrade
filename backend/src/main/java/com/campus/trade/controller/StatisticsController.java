package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Category;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.User;
import com.campus.trade.service.IOrderService;
import com.campus.trade.service.IProductService;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 */
/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/StatisticsController.java
 * hangu: 44 行 | kelei: 133 行 | 本文件合计: 177 行
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private com.campus.trade.service.ICategoryService categoryService;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> data = new HashMap<>();

        // 1. 统计用户总数
                                                                                                                                                                                                long userCount = userService.count();
        data.put("userCount", userCount);

        // 2. 统计在售商品数（状态为ON_SALE）
        QueryWrapper<Product> productWrapper = new QueryWrapper<>();
        productWrapper.eq("status", "ON_SALE");
        long productCount = productService.count(productWrapper);
        data.put("productCount", productCount);

        // 3. 统计已完成订单数（状态为COMPLETED）
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("status", "COMPLETED");
        long orderCount = orderService.count(orderWrapper);
        data.put("orderCount", orderCount);

        // 4. 统计交易总额（已完成订单的总金额）
        List<Order> completedOrders = orderService.list(orderWrapper);
        BigDecimal totalAmount = completedOrders.stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalAmount", totalAmount);

        // 5. 今日新增用户（可选，增加数据丰富度）
        // QueryWrapper<User> todayUserWrapper = new QueryWrapper<>();
        // todayUserWrapper.ge("create_time", LocalDate.now());
        // long todayUserCount = userService.count(todayUserWrapper);
        // data.put("todayUserCount", todayUserCount);

        // 6. 今日新增商品（可选）
        // QueryWrapper<Product> todayProductWrapper = new QueryWrapper<>();
        // todayProductWrapper.ge("create_time", LocalDate.now());
        // long todayProductCount = productService.count(todayProductWrapper);
        // data.put("todayProductCount", todayProductCount);

        return Result.success(data);
    }

    /**
     * 获取个人统计数据
     */
    @GetMapping("/user/my")
    public Result<Map<String, Object>> getMyStatistics() {
        // 获取当前登录用户
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error("请先登录");
                                                                                                                                                                                                }

        Map<String, Object> data = new HashMap<>();

        // 1. 发布的商品数
        QueryWrapper<Product> myProductWrapper = new QueryWrapper<>();
        myProductWrapper.eq("user_id", userId);
        long myProductCount = productService.count(myProductWrapper);
        data.put("myProductCount", myProductCount);

        // 2. 已售出的商品数
        QueryWrapper<Product> soldWrapper = new QueryWrapper<>();
        soldWrapper.eq("user_id", userId);
        soldWrapper.eq("status", "SOLD");
        long soldCount = productService.count(soldWrapper);
        data.put("soldCount", soldCount);

        // 3. 购买的订单数
        QueryWrapper<Order> myOrderWrapper = new QueryWrapper<>();
        myOrderWrapper.eq("buyer_id", userId);
        long myOrderCount = orderService.count(myOrderWrapper);
        data.put("myOrderCount", myOrderCount);

        // 4. 获得的收藏数（需要收藏表，暂时返回0）
        data.put("favoriteCount", 0);

        // 5. 信用分（从user_credit表获取，如果没有则返回默认值100）
        try {
            // 尝试获取用户信用分
            // 如果有UserCreditService，使用它；否则返回默认值
            data.put("creditScore", 100);
        } catch (Exception e) {
            data.put("creditScore", 100);
        }

        return Result.success(data);
    }

    /**
     * 获取Dashboard详细数据（最近订单、热门商品、待处理事项）
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 1. 最近10条订单
        QueryWrapper<Order> recentOrderWrapper = new QueryWrapper<>();
        recentOrderWrapper.orderByDesc("create_time").last("LIMIT 10");
                                                                                                                                                                                                List<Order> recentOrders = orderService.list(recentOrderWrapper);
        data.put("recentOrders", recentOrders);

        // 2. 待处理事项统计
        Map<String, Long> pendingTasks = new HashMap<>();

        // 待发货订单（已付款但未发货）
        QueryWrapper<Order> pendingShipWrapper = new QueryWrapper<>();
        pendingShipWrapper.eq("status", "PAID");
        long pendingShipCount = orderService.count(pendingShipWrapper);
        pendingTasks.put("pendingShip", pendingShipCount);

        // 待完成订单（已发货但未完成）
        QueryWrapper<Order> shippedWrapper = new QueryWrapper<>();
        shippedWrapper.eq("status", "SHIPPED");
        long shippedCount = orderService.count(shippedWrapper);
        pendingTasks.put("shipped", shippedCount);

        // 低库存商品（库存<=2）
        QueryWrapper<Product> lowStockWrapper = new QueryWrapper<>();
        lowStockWrapper.le("stock", 2).eq("status", "ON_SALE");
        long lowStockCount = productService.count(lowStockWrapper);
        pendingTasks.put("lowStock", lowStockCount);

        data.put("pendingTasks", pendingTasks);

        // 3. 热门商品Top5（按销量排序）
        // 这里简化处理，按浏览量排序
        QueryWrapper<Product> hotProductWrapper = new QueryWrapper<>();
        hotProductWrapper.eq("status", "ON_SALE")
                .orderByDesc("view_count")
                .last("LIMIT 5");
        List<Product> hotProducts = productService.list(hotProductWrapper);
        data.put("hotProducts", hotProducts);

        // 4. 最近注册用户（最新5个）
        QueryWrapper<User> recentUserWrapper = new QueryWrapper<>();
        recentUserWrapper.orderByDesc("create_time").last("LIMIT 5");
        List<User> recentUsers = userService.list(recentUserWrapper);
        data.put("recentUsers", recentUsers);

        return Result.success(data);
    }

    /**
     * 获取数据报表 - 交易趋势
     */
    @GetMapping("/charts/trade-trend")
    public Result<Map<String, Object>> getTradeTrend(@RequestParam(defaultValue = "7") Integer days) {
        Map<String, Object> data = new HashMap<>();

        // 获取最近N天的数据
        List<String> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();
                                                                                                                                                                                                LocalDateTime now = LocalDateTime.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            String dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd"));
            dateList.add(dateStr);

            // 查询当天的订单数量和交易金额
            LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            wrapper.eq("status", "COMPLETED")
                   .between("complete_time", startOfDay, endOfDay);

            List<Order> orders = orderService.list(wrapper);
            orderCountList.add(orders.size());

            BigDecimal dayAmount = orders.stream()
                    .map(Order::getTotalAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            amountList.add(dayAmount);
        }

        data.put("dates", dateList);
        data.put("orderCounts", orderCountList);
        data.put("amounts", amountList);

        return Result.success(data);
    }

    /**
     * 获取数据报表 - 商品分类占比
     */
    @GetMapping("/charts/category-distribution")
    public Result<List<Map<String, Object>>> getCategoryDistribution() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取所有分类
        List<Category> categories = categoryService.list();
        
        for (Category category : categories) {
            QueryWrapper<Product> wrapper = new QueryWrapper<>();
            wrapper.eq("category_id", category.getId());
            // 统计所有状态的商品，不只是在售的
            long count = productService.count(wrapper);
            
        if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", category.getName());
                item.put("value", count);
                result.add(item);
            }
        }
        
        return Result.success(result);
    }

    /**
     * 获取数据报表 - 热门商品排行
     */
    @GetMapping("/charts/hot-products")
    public Result<Map<String, Object>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> data = new HashMap<>();

        // 按浏览量排序获取热门商品
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "ON_SALE")
               .orderByDesc("view_count")
               .last("LIMIT " + limit);

        List<Product> products = productService.list(wrapper);

        List<String> names = new ArrayList<>();
        List<Integer> viewCounts = new ArrayList<>();

        for (Product product : products) {
            names.add(product.getTitle().length() > 10 ?
                     product.getTitle().substring(0, 10) + "..." :
                     product.getTitle());
            viewCounts.add(product.getViewCount() != null ? product.getViewCount() : 0);
        }

        data.put("names", names);
        data.put("viewCounts", viewCounts);

        return Result.success(data);
    }

    /**
     * 获取数据报表 - 用户增长趋势
     */
    @GetMapping("/charts/user-growth")
    public Result<Map<String, Object>> getUserGrowth(@RequestParam(defaultValue = "30") Integer days) {
        Map<String, Object> data = new HashMap<>();

        List<String> dateList = new ArrayList<>();
        List<Integer> userCountList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        int totalCount = 0;

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            String dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd"));
            dateList.add(dateStr);

            // 查询截止到当天的总用户数
            LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.le("create_time", endOfDay);

            totalCount = (int) userService.count(wrapper);
            userCountList.add(totalCount);
        }

        data.put("dates", dateList);
        data.put("userCounts", userCountList);

        return Result.success(data);
    }

    /**
     * 获取当前登录用户ID（简化版，实际应该从SecurityContext获取）
     */
    private Long getCurrentUserId() {
        // TODO: 从Spring Security或JWT中获取当前用户ID
        // 这里暂时返回null，实际使用时需要实现
        try {
            // 假设你使用了SecurityContextHolder
            // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            //     return ((User) authentication.getPrincipal()).getId();
            // }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}










