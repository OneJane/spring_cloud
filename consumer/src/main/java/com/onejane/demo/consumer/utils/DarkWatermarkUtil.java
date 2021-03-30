package com.onejane.demo.consumer.utils;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.Core.copyMakeBorder;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.putText;


public class DarkWatermarkUtil {
    private static List<Mat> planes = new ArrayList<Mat>();
    private static List<Mat> allPlanes = new ArrayList<Mat>();


    public static Mat addImageWatermarkWithText(Mat image, String watermarkText) {
        //优化图像的尺寸
        //Mat padded = optimizeImageDim(image);
        Mat complexImage = new Mat();
        Mat padded = splitSrc(image);
        padded.convertTo(padded, CvType.CV_32F);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        Core.merge(planes, complexImage);
        // dft
        Core.dft(complexImage, complexImage);
        // 添加文本水印
        Scalar scalar = new Scalar(0, 0, 0);
        Point point = new Point(60, 60);

        putText(complexImage, watermarkText, point, Core.FONT_HERSHEY_DUPLEX, 0.8D, scalar, 2);
        Core.flip(complexImage, complexImage, -1);

        putText(complexImage, watermarkText, point, Core.FONT_HERSHEY_DUPLEX, 0.8D, scalar, 2);
        Core.flip(complexImage, complexImage, -1);


        return antitransformImage(complexImage, allPlanes,padded);
    }

    public static Mat getImageWatermarkWithText(Mat image) {
        List<Mat> planes = new ArrayList<Mat>();
        Mat complexImage = new Mat();
        Mat padded = splitSrc(image);
        padded.convertTo(padded, CvType.CV_32F);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        Core.merge(planes, complexImage);
        // dft
        Core.dft(complexImage, complexImage);
        Mat magnitude = createOptimizedMagnitude(complexImage);
        planes.clear();
        return magnitude;
    }

    private static Mat splitSrc(Mat mat) {
//        mat = optimizeImageDim(mat);
        Mat padded = new Mat();
        Core.split(mat, allPlanes);
        if (allPlanes.size() > 1) {
            for (int i = 0; i < allPlanes.size(); i++) {
                if (i == 0) {
                    padded = allPlanes.get(i);
                    break;
                }
            }
        } else {
            padded = mat;
        }
        return padded;
    }

    private static Mat antitransformImage(Mat complexImage, List<Mat> allPlanes, Mat padded) {
        Mat invDFT = new Mat();
        Core.idft(complexImage, invDFT, Core.DFT_SCALE | Core.DFT_REAL_OUTPUT, 0);
        Mat restoredImage = new Mat();
        invDFT.convertTo(restoredImage, CvType.CV_8U);
        if (allPlanes.size() == 0) {
            allPlanes.add(restoredImage);
        } else {
            allPlanes.set(0, restoredImage);
        }
        Mat lastImage = new Mat();
        Core.merge(allPlanes, lastImage);
        planes.clear();
        allPlanes.clear();

        complexImage.release();
        invDFT.release();
        restoredImage.release();
        padded.release();
        return lastImage;
    }

    private static Mat optimizeImageDim(Mat image) {
        Mat padded = new Mat();
        int addPixelRows = Core.getOptimalDFTSize(image.rows());
        int addPixelCols = Core.getOptimalDFTSize(image.cols());
        copyMakeBorder(image, padded, 0, addPixelRows - image.rows(), 0, addPixelCols - image.cols(),
                BORDER_CONSTANT, Scalar.all(0));

        return padded;
    }

    private static Mat createOptimizedMagnitude(Mat complexImage) {
        List<Mat> newPlanes = new ArrayList<Mat>();
        Mat mag = new Mat();
        Core.split(complexImage, newPlanes);
        Core.magnitude(newPlanes.get(0), newPlanes.get(1), mag);
        Core.add(Mat.ones(mag.size(), CvType.CV_32F), mag, mag);
        Core.log(mag, mag);
        shiftDFT(mag);
        mag.convertTo(mag, CvType.CV_8UC1);
        Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
        return mag;
    }

    private static void shiftDFT(Mat image) {
        image = image.submat(new Rect(0, 0, image.cols() & -2, image.rows() & -2));
        int cx = image.cols() / 2;
        int cy = image.rows() / 2;

        Mat q0 = new Mat(image, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(image, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(image, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(image, new Rect(cx, cy, cx, cy));
        Mat tmp = new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
    }

    public static BufferedImage Mat2BufImg(Mat matrix, String fileExtension) {
        // convert the matrix into a matrix of bytes appropriate for
        // this file extension
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(fileExtension, matrix, mob);
        // convert the "matrix of bytes" into a byte array
        byte[] byteArray = mob.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImage;
    }


    public static Mat BufImg2Mat(BufferedImage original, int imgType, int matType) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }
//        System.loadLibrary("opencv_java342");
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.load("D:\\opencv3.4.2\\opencv\\build\\java\\x64\\opencv_java342.dll");
        //System.out.println(Core.NATIVE_LIBRARY_NAME);
        // Don't convert if it already has correct type
        if (original.getType() != imgType) {
            // Create a buffered image
            BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), imgType);

            // Draw the image onto the new buffer
            Graphics2D g = image.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(original, 0, 0, null);
            } finally {
                g.dispose();
            }
        }

        byte[] pixels = ((DataBufferByte) original.getRaster().getDataBuffer()).getData();
        Mat mat = Mat.eye(original.getHeight(), original.getWidth(), matType);
        mat.put(0, 0, pixels);
        return mat;
    }


    static {
        //加载opencv动态库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        // 加盲水印
        Mat encodeImg = Imgcodecs.imread("C:\\Users\\JDD\\Desktop\\DSC02804.JPG");
        Mat watermarkImg = addImageWatermarkWithText(encodeImg,"666");
        Imgcodecs.imwrite("s_encode.jpg", watermarkImg);

        // 解盲水印
        Mat outImg = Imgcodecs.imread("s_encode.jpg");
        Mat outWatermarkImg = getImageWatermarkWithText(outImg);
        Imgcodecs.imwrite("s_decode.jpg", outWatermarkImg);

    }
}