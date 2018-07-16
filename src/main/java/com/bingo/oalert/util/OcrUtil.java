package com.bingo.oalert.util;

import com.bingo.oalert.OalertApplication;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.LoadLibs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class OcrUtil {

    private static Logger logger = LoggerFactory.getLogger(OcrUtil.class);


    public static String ocrImg(String imagePath){
        logger.info("开始识别验证码");
        //1.读取图像
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new FileInputStream(imagePath));
            bufferedImage = enlargement(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = "";// 返回的验证码值
        ITesseract instance = new Tesseract();
        //这样就能使用classpath目录下的训练库了
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        instance.setLanguage("eng");
        instance.setTessVariable("tessedit_char_whitelist", "0123456789");
        instance.setPageSegMode(7);
        instance.setDatapath(tessDataFolder.getAbsolutePath());
        try {
            result = instance.doOCR(bufferedImage);//支持很多数据类型，比如：BufferedImage、File等
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result.replaceAll("\n|\r|\t| ","");
    }

    /**
     * 图片锐化与放大
     *
     * @return
     */
    protected static BufferedImage enlargement(BufferedImage image) {
        //灰度化
        image = ImageHelper.convertImageToGrayscale(ImageHelper.getSubImage(image, 0, 0, image.getWidth(), image.getHeight()));
        // 将图片扩大5倍
        image = ImageHelper.getScaledInstance(image, image.getWidth() * 5, image.getHeight() * 5);
        return image;
    }
}
