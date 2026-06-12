package com.campus.trade.controller;




import com.campus.trade.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 代码贡献统计（项目总计 3000 行）
 * 文件: backend/src/main/java/com/campus/trade/controller/FileController.java
 * hangu: 35 行 | kelei: 0 行 | 本文件合计: 35 行
 */
@RestController
@RequestMapping("/files")
public class FileController {
    
    @Value("${file.upload-path}")
    private String uploadPath;
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "type", defaultValue = "common") String type) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
                                                                                                                                                                                                // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 生成新文件名
        String fileName = UUID.randomUUID().toString() + suffix;
        
        // 按日期和类型分类
        String subPath = type + File.separator;
        
        // 实际存储路径
        File dest = new File(uploadPath + subPath + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
            // 返回访问URL (相对路径)
            String url = "/image/" + type + "/" + fileName;
            return Result.success(url);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }
}










