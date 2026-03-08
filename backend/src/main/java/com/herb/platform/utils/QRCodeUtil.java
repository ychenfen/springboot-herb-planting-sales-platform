package com.herb.platform.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 二维码工具类
 */
@Slf4j
@Component
public class QRCodeUtil {

    @Value("${herb.file.access-path:/uploads}")
    private String accessPath;

    @Value("${herb.file.storage-path:./uploads}")
    private String storagePath;

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;

    /**
     * 生成二维码图片并保存到文件
     */
    public String generateQRCode(String content) {
        try {
            BitMatrix bitMatrix = createBitMatrix(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);

            // 确保目录存在
            Path qrCodeDir = resolveStorageDir("qrcode");
            if (!Files.exists(qrCodeDir)) {
                Files.createDirectories(qrCodeDir);
            }

            // 生成文件名
            String fileName = UUID.randomUUID().toString().replace("-", "") + ".png";
            Path filePath = qrCodeDir.resolve(fileName);

            // 写入文件
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);

            return normalizeAccessPath(accessPath) + "/qrcode/" + fileName;
        } catch (WriterException | IOException e) {
            log.error("生成二维码失败", e);
            return null;
        }
    }

    /**
     * 生成二维码Base64字符串
     */
    public String generateQRCodeBase64(String content) {
        try {
            BitMatrix bitMatrix = createBitMatrix(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            log.error("生成二维码Base64失败", e);
            return null;
        }
    }

    /**
     * 创建BitMatrix
     */
    private BitMatrix createBitMatrix(String content, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        return qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
    }

    private Path resolveStorageDir(String subDir) {
        Path base = Paths.get(storagePath).toAbsolutePath().normalize();
        return base.resolve(subDir);
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
