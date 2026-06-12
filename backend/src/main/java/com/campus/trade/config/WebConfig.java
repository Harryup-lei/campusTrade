package com.campus.trade.config;




import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/config/WebConfig.java
 * hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /image/** 请求映射到本地上传目录
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:" + uploadPath);
    }
}










