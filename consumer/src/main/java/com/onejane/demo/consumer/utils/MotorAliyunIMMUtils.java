package com.onejane.demo.consumer.utils;

import com.aliyun.imm20170906.models.DecodeBlindWatermarkRequest;
import com.aliyun.imm20170906.models.EncodeBlindWatermarkRequest;
import com.onejane.demo.consumer.dto.AliyunBlindWaterDTO;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aliyun.teaopenapi.models.Config;
public class MotorAliyunIMMUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MotorAliyunIMMUtils.class);


    public static com.aliyun.imm20170906.Client getIMMClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId("AccessKeyId")
                .setAccessKeySecret("AccessKeySecret");
        config.endpoint = "imm.cn-beijing.aliyuncs.com";
        return new com.aliyun.imm20170906.Client(config);
    }

    public static String encodeBlindWatermark(AliyunBlindWaterDTO blindWaterDTO){
        EncodeBlindWatermarkRequest encodeBlindWatermarkRequest = new EncodeBlindWatermarkRequest();
        encodeBlindWatermarkRequest.setModel("DWT_IBG");
        encodeBlindWatermarkRequest.setImageUri(blindWaterDTO.getOriImageUri());
        encodeBlindWatermarkRequest.setContent(blindWaterDTO.getContent());
        encodeBlindWatermarkRequest.setProject("onejane");
        encodeBlindWatermarkRequest.setTargetUri(blindWaterDTO.getOriEncodeImageUri());
        try {
            return getIMMClient().encodeBlindWatermark(encodeBlindWatermarkRequest).getBody().getTargetUri();
        } catch (Exception e) {
            LOG.error("请求阿里加盲水印失败，{}",ExceptionUtils.getStackTrace(e));
            return "-1";
        }
    }

    public static String decodeBlindWatermark(AliyunBlindWaterDTO blindWaterDTO){
        DecodeBlindWatermarkRequest decodeBlindWatermarkRequest = new DecodeBlindWatermarkRequest();
        decodeBlindWatermarkRequest.setImageQuality(90);
        decodeBlindWatermarkRequest.setModel("DWT_IBG");
        decodeBlindWatermarkRequest.setImageUri(blindWaterDTO.getMarkEncodeImageUri());
        decodeBlindWatermarkRequest.setProject("onejane");
        decodeBlindWatermarkRequest.setTargetUri(blindWaterDTO.getMarkDecodeImageUri());
        try {
            return getIMMClient().decodeBlindWatermark(decodeBlindWatermarkRequest).getBody().getTargetUri();
        } catch (Exception e) {
            LOG.error("请求阿里加盲水印失败，{}", ExceptionUtils.getStackTrace(e));
            return "-1";
        }
    }


    public static void main(String ss[]) {
        try {
            AliyunBlindWaterDTO blindWaterDTO = new AliyunBlindWaterDTO();
            blindWaterDTO.setFileName("DSC02802.JPG");
            blindWaterDTO.setDate("20210315");
            blindWaterDTO.setContent("onejane");
            System.out.println(encodeBlindWatermark(blindWaterDTO));
            System.out.println(decodeBlindWatermark(blindWaterDTO));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}