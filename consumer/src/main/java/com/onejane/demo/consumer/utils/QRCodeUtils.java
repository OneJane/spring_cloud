package com.onejane.demo.consumer.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * 二维码工具
 */
public final class QRCodeUtils {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeUtils.class);

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    /**
     * 创建二维码
     * @param content
     * @param imgPath
     * @param needCompress
     * @return
     * @throws WriterException 
     * @throws URISyntaxException 
     * @throws Exception
     */
    private static BufferedImage createImage(String content, String imgPath, boolean needCompress) throws IOException, WriterException, URISyntaxException {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
//        int width = bitMatrix.getWidth();
//        int height = bitMatrix.getHeight();
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
//            }
//        }
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
//        MatrixToImageWriter.writeToPath(bitMatrix, FORMAT_NAME, Paths.get("E:/test.jpg"));
        // 插入图片
//        insertImage(image, imgPath, needCompress);
        return image;
    }

    /**
     * 二维码插入图片
     * @param source
     * @param imgPath
     * @param needCompress
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws IOException{
        
        if (StringUtils.isEmpty(imgPath)) {
            return;
        }
        
        File file = new File(imgPath);
        if (!file.exists()) {
            logger.warn("{}该文件不存在！", imgPath);
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            // 绘制缩小后的图
            g.drawImage(image, 0, 0, null); 
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * @param content
     * @param imgPath
     * @param needCompress
     * @return
     * @throws Exception
     */
    public static String encode(String content, String imgPath, boolean needCompress) {
        try {
            BufferedImage image = createImage(content, imgPath, needCompress);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, FORMAT_NAME, os);
            String base64 = Base64Utils.encodeToString(os.toByteArray());
            return base64;
//            return Base64.getUrlEncoder().encodeToString("abddd".getBytes());  
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("data:image/png;base64,"+encode("https://lite-pos.shouqianba.com/cashier/alipay?token=6bbb2452aeb21e2f9bab2ed2441234bf&csn=ceshi20210317151013&type=MINI",null,false));
    }

}
