package com.campus.trade.config;




import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/config/WebMvcConfig.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-path}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /image/** 到实际的文件存储目录
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:" + uploadPath);
        
        System.out.println("========== 静态资源配置 ==========");
        System.out.println("访问路径: /image/**");
        System.out.println("实际路径: file:" + uploadPath);
        System.out.println("===================================");
    }
}










