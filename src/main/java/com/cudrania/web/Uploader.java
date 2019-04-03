package com.cudrania.web;

import com.nianien.core.exception.ExceptionHandler;
import com.nianien.core.io.Closer;
import com.nianien.core.io.Files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 上传文件的工具类
 *
 * @author skyfalling
 */
public class Uploader {

    /**
     * 缓冲区1M
     */
    private static int bufferSize = 1024 * 1024;
    /**
     * 上传文件最大20M
     */
    private static long maxSize = 1024 * 1024 * 20;
    /**
     * 允许上传的文件类型
     */
    private static String[] typeAllowed = {".jpg", ".bmp", ".gif", ".doc",
            ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".pdf", ".rar", ".zip"};

    /**
     * 获取缓冲区大小
     *
     * @return int
     */
    public static int getBufferSize() {
        return bufferSize;
    }

    /**
     * 设置缓冲区大小
     *
     * @param bufferSize
     */
    public static void setBufferSize(int bufferSize) {
        Uploader.bufferSize = bufferSize;
    }

    /**
     * 获取允许上传文件的最大限制
     *
     * @return long
     */
    public static long getMaxSize() {
        return maxSize;
    }

    /**
     * 设置允许上传文件的最大限制
     *
     * @param maxSize
     */
    public static void setMaxSize(long maxSize) {
        Uploader.maxSize = maxSize;
    }

    /**
     * 获取允许的文件类型
     *
     * @return 文件后缀名数组
     */
    public static String[] getTypeAllowed() {
        return typeAllowed;
    }

    /**
     * 设置允许的文件类型
     *
     * @param typeAllowed
     */
    public static void setTypeAllowed(String[] typeAllowed) {
        Uploader.typeAllowed = typeAllowed;
    }

    /**
     * 上传文件
     *
     * @param file     待上传的文件对象
     * @param savePath 文件的存储路径,绝对路径或者相对于工程根目录的相对路径
     * @param fileName 上传文件的原始名称
     * @return 上传成功返回true, 否则false
     */
    public static boolean uploadFile(File file, String savePath,
                                     String fileName) {
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        boolean succeed = false;
        try {
            // 文件后缀名
            String fileExt = Files.getFileExt(fileName);
            is = new BufferedInputStream(new FileInputStream(file));
            // 获取文件大小
            long fileSize = file.length();

            checkUpload(fileSize, fileExt);

            File destFile = new File(savePath);
            os = new BufferedOutputStream(new FileOutputStream(destFile));
            byte[] buffer = new byte[bufferSize];
            int length = 0;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            succeed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Closer.close(os, is);
        }
        return succeed;
    }

    /**
     * 校验上传文件
     *
     * @param fileSize
     * @param fileExt
     */
    private static void checkUpload(long fileSize, String fileExt) {
        ExceptionHandler.throwIf(fileSize > maxSize, "文件大小超过最大限制："
                + fileSize + "!");
        if (typeAllowed == null || typeAllowed.length == 0)
            return;
        for (int i = 0; i < typeAllowed.length; i++) {
            if (typeAllowed[i].equals(fileExt.toLowerCase()))
                return;
        }
        ExceptionHandler.throwIf(true, "不允许的文件类型：" + fileExt + "！");
    }
}
