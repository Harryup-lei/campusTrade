package com.campus.trade.controller;




import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.User;
import com.campus.trade.entity.WantOffer;
import com.campus.trade.service.IUserService;
import com.campus.trade.service.IWantOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/WantOfferController.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@RestController
@RequestMapping("/want-offers")
public class WantOfferController {
    
    @Autowired
    private IWantOfferService wantOfferService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * 提交出价
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<String> submitOffer(@RequestBody WantOffer offer, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        offer.setSellerId(user.getId());
        
        boolean success = wantOfferService.submitOffer(offer);
        return success ? Result.success("出价提交成功") : Result.error("出价提交失败");
    }
    
    /**
     * 获取某个求购的所有出价
     */
    @GetMapping("/want/{wantId}")
    public Result<List<WantOffer>> getOffersByWantId(@PathVariable Long wantId) {
        List<WantOffer> offers = wantOfferService.getOffersByWantId(wantId);
        return Result.success(offers);
    }
    
    /**
     * 获取我的出价列表（卖家视角）
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Result<Page<WantOffer>> getMyOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Long userId = user.getId();
        Page<WantOffer> offers = wantOfferService.getMyOffers(userId, page, size);
        return Result.success(offers);
    }
    
    /**
     * 获取我收到的出价（买家视角）
     */
    @GetMapping("/received")
    @PreAuthorize("isAuthenticated()")
    public Result<Page<WantOffer>> getReceivedOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Long userId = user.getId();
        Page<WantOffer> offers = wantOfferService.getReceivedOffers(userId, page, size);
        return Result.success(offers);
    }
    
    /**
     * 接受出价
     */
    @PutMapping("/{offerId}/accept")
    @PreAuthorize("isAuthenticated()")
    public Result<String> acceptOffer(@PathVariable Long offerId, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Long userId = user.getId();
        boolean success = wantOfferService.acceptOffer(offerId, userId);
        return success ? Result.success("已接受出价") : Result.error("操作失败");
    }
    
    /**
     * 拒绝出价
     */
    @PutMapping("/{offerId}/reject")
    @PreAuthorize("isAuthenticated()")
    public Result<String> rejectOffer(@PathVariable Long offerId, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Long userId = user.getId();
        boolean success = wantOfferService.rejectOffer(offerId, userId);
        return success ? Result.success("已拒绝出价") : Result.error("操作失败");
    }
    
    /**
     * 删除出价（卖家删除自己的出价）
     */
    @DeleteMapping("/{offerId}")
    @PreAuthorize("isAuthenticated()")
    public Result<String> deleteOffer(@PathVariable Long offerId, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证这个出价是否属于当前用户
        WantOffer offer = wantOfferService.getById(offerId);
        if (offer == null) {
            return Result.error("出价不存在");
        }
        if (!offer.getSellerId().equals(user.getId())) {
            return Result.error("无权删除此出价");
        }
        
        boolean success = wantOfferService.removeById(offerId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}










