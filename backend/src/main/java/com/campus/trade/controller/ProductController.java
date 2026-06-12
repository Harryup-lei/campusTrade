package com.campus.trade.controller;




import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Product;
import com.campus.trade.entity.Category;
import com.campus.trade.service.ICategoryService;
import com.campus.trade.service.IProductService;
import com.campus.trade.entity.User;
import com.campus.trade.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/ProductController.java
 * hangu: 51 行 | kelei: 149 行 | 本文件合计: 200 行
 */
@RestController
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private IProductService productService;
    
    @Autowired
    private ICategoryService categoryService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private com.campus.trade.service.ICreditService creditService;
    
    @Autowired
    private com.campus.trade.service.IOperationLogService operationLogService;
    @GetMapping
    public Result<?> getProducts(
                                                                                                @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long categoryId,
                                @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) String conditionDesc,
            @RequestParam(required = false) String sort) {
        
        System.out.println("========== 查询商品列表 ==========");
        System.out.println("page: " + page + ", size: " + size);
        System.out.println("categoryId: " + categoryId);
        System.out.println("keyword: " + keyword);
        System.out.println("status: " + status);
        System.out.println("minPrice: " + minPrice + ", maxPrice: " + maxPrice);
        System.out.println("conditionDesc: " + conditionDesc);
        System.out.println("sort: " + sort);
        
        // MyBatis Plus的Page页码从1开始，但前端传的是从0开始
        Page<Product> pageParam = new Page<>(page + 1, size);
        
        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Product> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        // 状态筛选（默认只查询在售商品）
        String queryStatus = (status != null && !status.isEmpty()) ? status : "ON_SALE";
        
        // 管理员可以查询所有商品
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin || status != null) {
            queryWrapper.eq("status", queryStatus);
        }
        
        // 分类筛选
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("title", keyword);
        }
        
        // 价格区间筛选
        if (minPrice != null) {
            queryWrapper.ge("price", minPrice);
        }
        if (maxPrice != null) {
            queryWrapper.le("price", maxPrice);
        }
        
        // 成色筛选
        if (conditionDesc != null && !conditionDesc.isEmpty()) {
            queryWrapper.eq("condition_desc", conditionDesc);
        }
        
        // 排序
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "newest":
                    // 最新发布
                    queryWrapper.orderByDesc("create_time");
                    break;
                case "price_asc":
                    // 价格升序
                    queryWrapper.orderByAsc("price");
                    break;
                case "price_desc":
                    // 价格降序
                    queryWrapper.orderByDesc("price");
                    break;
                default:
                    // 综合排序（默认：创建时间降序）
                    queryWrapper.orderByDesc("create_time");
                    break;
            }
        } else {
            // 默认按创建时间降序
            queryWrapper.orderByDesc("create_time");
        }
        
        IPage<Product> products = productService.page(pageParam, queryWrapper);
        
        // 填充关联信息（category和seller）
        for (Product product : products.getRecords()) {
        if (product.getCategoryId() != null) {
                product.setCategory(categoryService.getById(product.getCategoryId()));
            }
        if (product.getSellerId() != null) {
                product.setSeller(userService.getById(product.getSellerId()));
            }
        }
        
        System.out.println("查询到商品数量: " + products.getRecords().size());
        System.out.println("总商品数: " + products.getTotal());
        System.out.println("========================================");
        
        return Result.success(products);
    }
    
    @GetMapping("/{id}")
    public Result<?> getProduct(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product != null) {
            // 填充关联信息
        if (product.getCategoryId() != null) {
                product.setCategory(categoryService.getById(product.getCategoryId()));
            }
        if (product.getSellerId() != null) {
                product.setSeller(userService.getById(product.getSellerId()));
            }
            
            product.setViewCount(product.getViewCount() + 1);
            productService.updateById(product);
            
            // 记录日志
            operationLogService.log("VIEW", "PRODUCT", "浏览商品：" + product.getTitle());
            
            return Result.success(product);
        }
        return Result.error("商品不存在");
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
        public Result<?> createProduct(@RequestBody Product product) {
        // 如果没有指定sellerId，则使用当前登录用户
        if (product.getSellerId() == null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;
            
                                                                                                                                                                                                if (principal instanceof org.springframework.security.core.userdetails.User) {
                    username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            } else {
                username = principal.toString();
            }
            
            User currentUser = userService.findByUsername(username);
        if (currentUser != null) {
                product.setSellerId(currentUser.getId());
            } else {
                return Result.error("用户不存在");
            }
        }
        
        // 设置默认状态为在售
        if (product.getStatus() == null || product.getStatus().isEmpty()) {
            product.setStatus("ON_SALE");
        }
        
        // 设置默认浏览次数
        if (product.getViewCount() == null) {
            product.setViewCount(0);
        }
        
        productService.save(product);
        
        // 记录日志
        operationLogService.log("CREATE", "PRODUCT", "发布商品：" + product.getTitle());
        
        // 发布商品增加信用分
        try {
            creditService.addCreditScore(
                product.getSellerId(), 
                5, 
                "发布商品", 
                "BEHAVIOR", 
                product.getId()
            );
        } catch (Exception e) {
            System.err.println("增加信用分失败: " + e.getMessage());
        }
        
        return Result.success(product);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
        public Result<?> updateProduct(@PathVariable Long id, @RequestBody Product product, Authentication authentication) {
        System.out.println("========== 更新商品 ==========");
        System.out.println("商品ID: " + id);
        System.out.println("当前用户: " + authentication.getName());
        
        Product existing = productService.getById(id);
        if (existing == null) {
                                                                                                                                                                                                return Result.error("商品不存在");
            }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限：只有商品所有者或管理员可以更新
        if (!existing.getSellerId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals("ADMIN")) {
            System.out.println("权限不足：当前用户ID=" + currentUser.getId() + ", 商品所有者ID=" + existing.getSellerId());
            return Result.error("无权限修改此商品");
        }
        
        existing.setTitle(product.getTitle());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setOriginalPrice(product.getOriginalPrice());
        existing.setStock(product.getStock());
        existing.setImages(product.getImages());
        existing.setConditionDesc(product.getConditionDesc());
        existing.setLocation(product.getLocation());
        existing.setCategoryId(product.getCategoryId());
        
        productService.updateById(existing);
        
        // 记录日志
        operationLogService.log("UPDATE", "PRODUCT", "更新商品：" + existing.getTitle());
        
        System.out.println("商品更新成功");
        System.out.println("================================");
        
        return Result.success(existing);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<?> deleteProduct(@PathVariable Long id, Authentication authentication) {
        System.out.println("========== 删除商品 ==========");
        System.out.println("商品ID: " + id);
        
        Product existing = productService.getById(id);
        if (existing == null) {
            return Result.error("商品不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限：只有商品所有者或管理员可以删除
        if (!existing.getSellerId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals("ADMIN")) {
            return Result.error("无权限删除此商品");
        }
        
        Product product = existing;  // 保存引用用于日志
        productService.removeById(id);
        
        // 记录日志
        operationLogService.log("DELETE", "PRODUCT", "删除商品：" + product.getTitle() + "(ID:" + id + ")");
        
        System.out.println("商品删除成功");
        System.out.println("================================");
        
        return Result.success("删除成功");
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public Result<?> updateProductStatus(@PathVariable Long id, @RequestParam String status, Authentication authentication) {
        System.out.println("========== 更新商品状态 ==========");
        System.out.println("商品ID: " + id);
        System.out.println("新状态: " + status);
        
        Product existing = productService.getById(id);
        if (existing == null) {
            return Result.error("商品不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限：只有商品所有者或管理员可以修改状态
        if (!existing.getSellerId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals("ADMIN")) {
            return Result.error("无权限操作此商品");
        }
        
        existing.setStatus(status);
        productService.updateById(existing);
        
        // 记录日志
        String statusText = "ON_SALE".equals(status) ? "上架" : "下架";
        operationLogService.log("UPDATE", "PRODUCT", statusText + "商品：" + existing.getTitle());
        
        System.out.println("商品状态更新成功");
        System.out.println("================================");
        
        return Result.success("状态更新成功");
    }
    
    @PutMapping("/{id}/on-sale")
    @PreAuthorize("isAuthenticated()")
    public Result<?> onSaleProduct(@PathVariable Long id, Authentication authentication) {
        System.out.println("========== 上架商品 ==========");
        System.out.println("商品ID: " + id);
        
        Product existing = productService.getById(id);
        if (existing == null) {
            return Result.error("商品不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限：只有商品所有者或管理员可以上架
        if (!existing.getSellerId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals("ADMIN")) {
            return Result.error("无权限操作此商品");
        }
        
        existing.setStatus("ON_SALE");
        productService.updateById(existing);
        
        System.out.println("商品上架成功");
        System.out.println("================================");
        
        return Result.success("上架成功");
    }
    
    @PutMapping("/{id}/off-sale")
    @PreAuthorize("isAuthenticated()")
    public Result<?> offSaleProduct(@PathVariable Long id, Authentication authentication) {
        System.out.println("========== 下架商品 ==========");
        System.out.println("商品ID: " + id);
        
        Product existing = productService.getById(id);
        if (existing == null) {
            return Result.error("商品不存在");
        }
        
        // 获取当前用户
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限：只有商品所有者或管理员可以下架
        if (!existing.getSellerId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals("ADMIN")) {
            return Result.error("无权限操作此商品");
        }
        
        existing.setStatus("OFF_SALE");
        productService.updateById(existing);
        
        System.out.println("商品下架成功");
        System.out.println("================================");
        
        return Result.success("下架成功");
    }
    
    @GetMapping("/seller/{sellerId}")
    public Result<?> getSellerProducts(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Product> pageParam = new Page<>(page, size);
        IPage<Product> products = productService.findBySellerId(sellerId, pageParam);
        return Result.success(products);
    }
}










