package com.herb.platform.controller;

import com.herb.platform.common.Result;
import com.herb.platform.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @ApiOperation("上传单个文件")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Result<String> upload(
            @RequestPart("file") MultipartFile file,
            @ApiParam("子目录") @RequestParam(defaultValue = "images") String subDir) {
        return Result.success(fileService.upload(file, subDir));
    }

    @ApiOperation("上传图片")
    @PostMapping(value = "/upload/image", consumes = "multipart/form-data")
    public Result<String> uploadImage(@RequestPart("file") MultipartFile file) {
        return Result.success(fileService.upload(file, "images"));
    }

    @ApiOperation("批量上传文件")
    @PostMapping(value = "/upload/batch", consumes = "multipart/form-data")
    public Result<List<String>> uploadBatch(
            @RequestPart("files") MultipartFile[] files,
            @ApiParam("子目录") @RequestParam(defaultValue = "images") String subDir) {
        return Result.success(fileService.uploadBatch(files, subDir));
    }

    @ApiOperation("删除文件")
    @DeleteMapping
    public Result<Boolean> delete(@ApiParam("文件URL") @RequestParam String fileUrl) {
        return Result.success(fileService.delete(fileUrl));
    }
}
