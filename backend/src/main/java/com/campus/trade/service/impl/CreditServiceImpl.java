package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.entity.CreditRecord;
import com.campus.trade.entity.UserCredit;
import com.campus.trade.mapper.CreditRecordMapper;
import com.campus.trade.mapper.UserCreditMapper;
import com.campus.trade.service.ICreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/CreditServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class CreditServiceImpl implements ICreditService {
    
    @Autowired
    private UserCreditMapper userCreditMapper;
    
    @Autowired
    private CreditRecordMapper creditRecordMapper;
    
    @Override
    public UserCredit getUserCredit(Long userId) {
        QueryWrapper<UserCredit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserCredit userCredit = userCreditMapper.selectOne(queryWrapper);
        
        // 如果用户信用不存在，初始化
        if (userCredit == null) {
            userCredit = initUserCredit(userId);
        }
        
        return userCredit;
    }
    
    @Override
    @Transactional
    public UserCredit initUserCredit(Long userId) {
        UserCredit userCredit = new UserCredit();
        userCredit.setUserId(userId);
        userCredit.setCreditScore(100); // 初始100分
        userCredit.setLevel("GOOD");
        userCredit.setTotalEarned(0);
        userCredit.setTotalDeducted(0);
        
        userCreditMapper.insert(userCredit);
        return userCredit;
    }
    
    @Override
    @Transactional
    public void addCreditScore(Long userId, Integer score, String reason, String reasonType, Long relatedId) {
        UserCredit userCredit = getUserCredit(userId);
        
        int beforeScore = userCredit.getCreditScore();
        int afterScore = beforeScore + score;
        
        // 更新信用分
        userCredit.setCreditScore(afterScore);
        userCredit.setTotalEarned(userCredit.getTotalEarned() + score);
        userCreditMapper.updateById(userCredit);
        
        // 记录变更
        CreditRecord record = new CreditRecord();
        record.setUserId(userId);
        record.setScoreChange(score);
        record.setReason(reason);
        record.setReasonType(reasonType);
        record.setRelatedId(relatedId);
        record.setBeforeScore(beforeScore);
        record.setAfterScore(afterScore);
        creditRecordMapper.insert(record);
        
        // 更新等级
        updateCreditLevel(userId);
    }
    
    @Override
    @Transactional
    public void deductCreditScore(Long userId, Integer score, String reason, String reasonType, Long relatedId) {
        UserCredit userCredit = getUserCredit(userId);
        
        int beforeScore = userCredit.getCreditScore();
        int afterScore = Math.max(0, beforeScore - score); // 不能低于0
        
        // 更新信用分
        userCredit.setCreditScore(afterScore);
        userCredit.setTotalDeducted(userCredit.getTotalDeducted() + score);
        userCreditMapper.updateById(userCredit);
        
        // 记录变更
        CreditRecord record = new CreditRecord();
        record.setUserId(userId);
        record.setScoreChange(-score);
        record.setReason(reason);
        record.setReasonType(reasonType);
        record.setRelatedId(relatedId);
        record.setBeforeScore(beforeScore);
        record.setAfterScore(afterScore);
        creditRecordMapper.insert(record);
        
        // 更新等级
        updateCreditLevel(userId);
    }
    
    @Override
    public IPage<CreditRecord> getCreditRecords(Long userId, int page, int size) {
        Page<CreditRecord> pageParam = new Page<>(page, size);
        QueryWrapper<CreditRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        
        return creditRecordMapper.selectPage(pageParam, queryWrapper);
    }
    
    @Override
    @Transactional
    public void updateCreditLevel(Long userId) {
        UserCredit userCredit = getUserCredit(userId);
        int score = userCredit.getCreditScore();
        
        String newLevel;
        if (score >= 150) {
            newLevel = "EXCELLENT"; // 优秀
        } else if (score >= 100) {
            newLevel = "GOOD"; // 良好
        } else if (score >= 60) {
            newLevel = "NORMAL"; // 一般
        } else {
            newLevel = "BAD"; // 较差
        }
        
        if (!newLevel.equals(userCredit.getLevel())) {
            userCredit.setLevel(newLevel);
            userCreditMapper.updateById(userCredit);
        }
    }
    
    @Override
    public boolean hasTodayRecord(Long userId, String reason) {
        QueryWrapper<CreditRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("reason", reason);
        
        // 获取今天的开始时间（00:00:00）
        java.time.LocalDateTime todayStart = java.time.LocalDate.now().atStartOfDay();
        queryWrapper.ge("create_time", todayStart);
        
        // 查询今天是否有记录
        Long count = creditRecordMapper.selectCount(queryWrapper);
        return count > 0;
    }
}










