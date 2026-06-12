package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/ICreditService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.entity.CreditRecord;
import com.campus.trade.entity.UserCredit;

public interface ICreditService {
    
    /**
     * 获取用户信用信息
     */
    UserCredit getUserCredit(Long userId);
    
    /**
     * 初始化用户信用（新用户注册时调用）
     */
    UserCredit initUserCredit(Long userId);
    
    /**
     * 增加信用分
     */
    void addCreditScore(Long userId, Integer score, String reason, String reasonType, Long relatedId);
    
    /**
     * 扣除信用分
     */
    void deductCreditScore(Long userId, Integer score, String reason, String reasonType, Long relatedId);
    
    /**
     * 获取信用记录（分页）
     */
    IPage<CreditRecord> getCreditRecords(Long userId, int page, int size);
    
    /**
     * 更新信用等级
     */
    void updateCreditLevel(Long userId);
    
    /**
     * 检查今日是否已有指定原因的记录
     */
    boolean hasTodayRecord(Long userId, String reason);
}










