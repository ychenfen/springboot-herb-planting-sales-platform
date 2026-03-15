package com.herb.platform.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传服务接口
 */
public interface FileService {

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @param subDir 子目录（如 images, documents）
     * @return 文件访问URL
     */
    String upload(MultipartFile file, String subDir);

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @param subDir 子目录
     * @return 文件访问URL列表
     */
    List<String> uploadBatch(MultipartFile[] files, String subDir);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean delete(String fileUrl);
}
