package com.campus.trade.controller;




import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.WantOffer;
import com.campus.trade.service.IWantOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/AdminWantOfferController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/admin/want-offers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWantOfferController {
    
    @Autowired
    private IWantOfferService wantOfferService;
    
    /**
     * 分页查询出价列表
     */
    @GetMapping
    public Result<Page<WantOffer>> getOfferPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        
        Page<WantOffer> offerPage = new Page<>(page, size);
        
        // 如果有状态筛选，可以在这里添加查询条件
        // 暂时返回所有数据
        Page<WantOffer> result = wantOfferService.page(offerPage);
        
        return Result.success(result);
    }
    
    /**
     * 获取出价详情
     */
    @GetMapping("/{id}")
    public Result<WantOffer> getOfferById(@PathVariable Long id) {
        WantOffer offer = wantOfferService.getById(id);
        if (offer == null) {
            return Result.error("出价不存在");
        }
        return Result.success(offer);
    }
    
    /**
     * 删除出价（管理员权限）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteOffer(@PathVariable Long id) {
        boolean success = wantOfferService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}










