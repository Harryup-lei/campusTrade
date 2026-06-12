package com.campus.trade;




import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/CampusTradeApplication.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@SpringBootApplication
@MapperScan("com.campus.trade.mapper")
public class CampusTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusTradeApplication.class, args);
        System.out.println("校园二手交易平台成功启动！！");
    }
}










