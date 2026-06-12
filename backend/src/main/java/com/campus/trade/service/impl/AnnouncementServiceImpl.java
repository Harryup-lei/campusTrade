package com.campus.trade.service.impl;




import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Announcement;
import com.campus.trade.mapper.AnnouncementMapper;
import com.campus.trade.service.IAnnouncementService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/impl/AnnouncementServiceImpl.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements IAnnouncementService {
    
    @Override
    public List<Announcement> getActiveAnnouncements(int limit) {
        LocalDateTime now = LocalDateTime.now();
        
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, "ACTIVE")
                .and(w -> w.isNull(Announcement::getStartTime)
                        .or()
                        .le(Announcement::getStartTime, now))
                .and(w -> w.isNull(Announcement::getEndTime)
                        .or()
                        .ge(Announcement::getEndTime, now))
                .orderByDesc(Announcement::getPriority)
                .orderByDesc(Announcement::getCreateTime)
                .last("LIMIT " + limit);
        
        return list(wrapper);
    }
    
    @Override
    public Page<Announcement> getAnnouncementPage(int page, int size, String status, String type) {
        Page<Announcement> announcementPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Announcement::getStatus, status);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Announcement::getType, type);
        }
        wrapper.orderByDesc(Announcement::getPriority)
                .orderByDesc(Announcement::getCreateTime);
        
        return page(announcementPage, wrapper);
    }
    
    @Override
    public boolean updateStatus(Long id, String status) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            return false;
        }
        announcement.setStatus(status);
        return updateById(announcement);
    }
    
    @Override
    public boolean incrementViewCount(Long id) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            return false;
        }
        announcement.setViewCount(announcement.getViewCount() + 1);
        return updateById(announcement);
    }
}










