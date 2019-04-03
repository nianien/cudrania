package com.cudrania.web;

import com.nianien.core.io.Closer;
import com.nianien.core.io.Files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 下载文件的工具类
 *
 * @author skyfalling
 */
public class Downloader {
    /**
     * 缓冲区1M
     */
    private static int bufferSize = 1024 * 1024;

    /**
     * 获取缓冲区大小
     *
     * @return int
     */
    public static int getBufferSize() {
        return bufferSize;
    }

    /**
     * 下载文件
     *
     * @param filePath    文件的绝对路径或者相对于工程根目录的相对路径
     * @param displayName 下载时显示的文件名称
     * @param request     请求对象
     * @param response    响应对象
     * @return 下载成功返回true, 否则false
     */
    public static boolean download(String filePath, String displayName,
                                   HttpServletRequest request, HttpServletResponse response) {
        BufferedInputStream is;
        try {
            String savePath = getSavePath(filePath, request.getSession()
                    .getServletContext());
            is = new BufferedInputStream(new FileInputStream(savePath));
            return download(is, displayName, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 将输入流对象输出到客户端,然后关闭输入流
     *
     * @param inputStream 输入流
     * @param displayName 下载时显示的文件名称
     * @param response    响应对象
     * @return 下载成功返回true, 否则false
     */
    public static boolean download(InputStream inputStream, String displayName, HttpServletResponse response) {
        BufferedOutputStream os = null;
        BufferedInputStream is = null;
        byte[] buffer = new byte[bufferSize];
        int byteRead = -1;
        try {
            is = new BufferedInputStream(inputStream);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=\""
                    + displayName + "\"");
            os = new BufferedOutputStream(response.getOutputStream());
            while ((byteRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, byteRead);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            Closer.close(os, is);
        }
    }

    /**
     * 获取文件的存储路径
     *
     * @param filePath
     * @param context
     * @return 文件的存储路径
     */
    private static String getSavePath(String filePath, ServletContext context) {
        return Files.isAbsolutePath(filePath) ? filePath : context
                .getRealPath(filePath);
    }
}
