package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/IAnnouncementService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Announcement;

import java.util.List;

public interface IAnnouncementService extends IService<Announcement> {
    
    /**
     * 获取启用的公告列表（用于前端展示）
     */
    List<Announcement> getActiveAnnouncements(int limit);
    
    /**
     * 分页查询公告（管理后台）
     */
    Page<Announcement> getAnnouncementPage(int page, int size, String status, String type);
    
    /**
     * 更新公告状态
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * 增加浏览次数
     */
    boolean incrementViewCount(Long id);
}










