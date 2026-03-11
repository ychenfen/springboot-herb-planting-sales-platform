package com.herb.platform.utils;

import com.herb.platform.exception.BusinessException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Lightweight image feature extraction for local similarity matching.
 */
public final class ImageFeatureUtil {

    private static final int BINS = 4;

    private ImageFeatureUtil() {
    }

    public static double[] extractFeature(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new BusinessException("无法识别上传图片，请使用 PNG/JPG 格式");
            }
            int width = image.getWidth();
            int height = image.getHeight();
            double[] histogram = new double[BINS * BINS * BINS];
            double pixelCount = Math.max(1, width * height);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    int index = toBin(r) * BINS * BINS + toBin(g) * BINS + toBin(b);
                    histogram[index] += 1D;
                }
            }
            for (int i = 0; i < histogram.length; i++) {
                histogram[i] = histogram[i] / pixelCount;
            }
            return histogram;
        } catch (IOException e) {
            throw new BusinessException("图片解析失败: " + e.getMessage());
        }
    }

    public static double cosineSimilarity(double[] left, double[] right) {
        double dot = 0D;
        double leftNorm = 0D;
        double rightNorm = 0D;
        for (int i = 0; i < left.length; i++) {
            dot += left[i] * right[i];
            leftNorm += left[i] * left[i];
            rightNorm += right[i] * right[i];
        }
        if (leftNorm == 0D || rightNorm == 0D) {
            return 0D;
        }
        return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }

    private static int toBin(int channelValue) {
        return Math.min(BINS - 1, channelValue * BINS / 256);
    }
}
