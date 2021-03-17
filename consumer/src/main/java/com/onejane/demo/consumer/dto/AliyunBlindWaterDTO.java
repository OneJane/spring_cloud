package com.onejane.demo.consumer.dto;

import java.io.Serializable;


public class AliyunBlindWaterDTO implements Serializable {

    private String prifix = "oss";
    private String bucketName = "onejane-opencv";
    private String encodeBucketName = "onejane-opencv";
    private String folder = "blind";
    private String date;
    private String content;
    private String fileName;

    public AliyunBlindWaterDTO() {
    }

    private String getEncodeFileName(){
        return fileName.replace(".JPG","_encode.JPG");
    }

    private String getDecodeFileName(){
        return fileName.replace(".JPG","_decode.JPG");
    }

    // oss://jddmoto-private/carport/20210315/282272358337600.jpg
    public String getOriImageUri(){
        return new StringBuilder().append(prifix).append("://").append(bucketName).append("/").append(folder).append("/").append(date).append("/").append(fileName).toString();
    }

    // oss://jddmoto-private/carport/20210315/282272358337600_encode.jpg
    public String getOriEncodeImageUri(){
        return new StringBuilder().append(prifix).append("://").append(encodeBucketName).append("/").append(folder).append("/").append(date).append("/").append(getEncodeFileName()).toString();
    }

    // oss://jddmoto-private/carport/20210315/282272358337600_decode.jpg
    public String getMarkEncodeImageUri(){
        return new StringBuilder().append(prifix).append("://").append(encodeBucketName).append("/").append(folder).append("/").append(date).append("/").append(getEncodeFileName()).toString();
    }

    // oss://jddmoto-private/carport/20210315/282272358337600_decode.jpg
    public String getMarkDecodeImageUri(){
        return new StringBuilder().append(prifix).append("://").append(encodeBucketName).append("/").append(folder).append("/").append(date).append("/").append(getDecodeFileName()).toString();
    }

    public String getPrifix() {
        return prifix;
    }

    public void setPrifix(String prifix) {
        this.prifix = prifix;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
