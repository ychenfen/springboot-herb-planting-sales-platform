package com.herb.platform.service.impl;

import com.herb.platform.common.Constants;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${herb.file.storage-path:./uploads}")
    private String storagePath;

    @Value("${herb.file.access-path:/uploads}")
    private String accessPath;

    @Value("${herb.file.max-size:10485760}")
    private long maxFileSize;

    @Override
    public String upload(MultipartFile file, String subDir) {
        validateFile(file);
        try {
            // 生成存储路径：子目录/日期/UUID.扩展名
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;

            // 构建完整存储路径
            Path targetDir = Paths.get(storagePath).toAbsolutePath().normalize()
                    .resolve(subDir != null ? subDir : "files")
                    .resolve(datePath);

            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            Path targetPath = targetDir.resolve(newFileName);
            file.transferTo(targetPath.toFile());

            // 返回访问URL
            String normalizedAccessPath = normalizeAccessPath(accessPath);
            String relativePath = (subDir != null ? subDir : "files") + "/" + datePath + "/" + newFileName;
            String url = normalizedAccessPath + "/" + relativePath;

            log.info("文件上传成功: {} -> {}", originalFilename, url);
            return url;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<String> uploadBatch(MultipartFile[] files, String subDir) {
        if (files == null || files.length == 0) {
            throw new BusinessException("请选择要上传的文件");
        }
        if (files.length > 10) {
            throw new BusinessException("一次最多上传10个文件");
        }
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(upload(file, subDir));
        }
        return urls;
    }

    @Override
    public boolean delete(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return false;
        }
        try {
            String normalizedAccessPath = normalizeAccessPath(accessPath);
            String relativePath = fileUrl.replace(normalizedAccessPath + "/", "");
            Path filePath = Paths.get(storagePath).toAbsolutePath().normalize().resolve(relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功: {}", fileUrl);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("文件删除失败: {}", fileUrl, e);
            return false;
        }
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("文件大小不能超过" + (maxFileSize / 1024 / 1024) + "MB");
        }
        String extension = getFileExtension(file.getOriginalFilename());
        boolean isAllowed = Arrays.stream(Constants.ALLOWED_FILE_TYPES)
                .anyMatch(type -> type.equalsIgnoreCase(extension));
        if (!isAllowed) {
            throw new BusinessException("不支持的文件类型: " + extension
                    + "，允许的类型: " + String.join(", ", Constants.ALLOWED_FILE_TYPES));
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String normalizeAccessPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "/uploads";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
