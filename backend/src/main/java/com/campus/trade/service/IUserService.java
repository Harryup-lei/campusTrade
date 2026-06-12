package com.campus.trade.service;








/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/service/IUserService.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.User;
import java.util.Map;

public interface IUserService extends IService<User> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Map<String, Object> getUserStats(Long userId);
}










