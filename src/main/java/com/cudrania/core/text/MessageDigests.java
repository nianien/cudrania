package com.cudrania.core.text;

import com.cudrania.core.bits.ByteUtils;
import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.io.Closer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 计算文本摘要的工具类<br/>
 * 该类是线程安全的
 *
 * @author skyfalling
 */
public class MessageDigests {

    private static ThreadLocal<MessageDigest> md5 = new ThreadLocal<MessageDigest>() {
        @Override
        protected MessageDigest initialValue() {
            return md5();
        }
    };
    private static ThreadLocal<MessageDigest> sha = new ThreadLocal<MessageDigest>() {
        @Override
        protected MessageDigest initialValue() {
            return sha();
        }
    };

    /**
     * 计算文本的MD5值
     *
     * @param content
     * @return
     */
    public static String md5(String content) {
        MessageDigest md = md5.get();
        md.reset();
        return ByteUtils.byte2Hex(digest(md, content));
    }

    /**
     * 计算文本的MD5值
     *
     * @param content
     * @param charset 文本编码
     * @return
     */
    public static String md5(String content, String charset) {
        MessageDigest md = md5.get();
        md.reset();
        return ByteUtils.byte2Hex(digest(md, content, charset));
    }

    /**
     * 计算文件的MD5值
     *
     * @param file
     * @return
     */
    public static String md5(File file) {
        MessageDigest md = md5.get();
        md.reset();
        return ByteUtils.byte2Hex(digest(md, file));
    }


    /**
     * 计算字节数组的MD5值
     *
     * @param bytes
     * @return
     */
    public static byte[] md5(byte[] bytes) {
        MessageDigest md = md5.get();
        md.reset();
        return md.digest(bytes);
    }


    /**
     * 计算文本的SHA值
     *
     * @param content
     * @return
     */
    public static String sha(String content) {
        MessageDigest md = sha.get();
        md.reset();
        return ByteUtils.byte2Hex(digest(md, content));
    }

    /**
     * 计算文本的SHA值
     *
     * @param content
     * @param charset 文本编码
     * @return
     */
    public static String sha(String content, String charset) {
        MessageDigest md = sha.get();
        md.reset();
        return ByteUtils.byte2Hex(digest(md, content, charset));
    }


    /**
     * 计算文件的SHA值
     *
     * @param file
     * @return
     */
    public static String sha(File file) {
        MessageDigest md = sha.get();
        md.reset();
        return ByteUtils.byte2Hex(digest(md, file));
    }

    /**
     * 计算字节数组的SHA值
     *
     * @param bytes
     * @return
     */
    public static byte[] sha(byte[] bytes) {
        MessageDigest md = sha.get();
        md.reset();
        return md.digest(bytes);
    }

    /**
     * 计算文本摘要
     *
     * @param md
     * @param content
     * @return
     */
    public static byte[] digest(MessageDigest md, String content) {
        return md.digest(content.getBytes());
    }

    /**
     * 计算文本摘要
     *
     * @param md
     * @param content
     * @param charset 文本编码
     * @return
     */
    public static byte[] digest(MessageDigest md, String content, String charset) {
        try {
            return md.digest(content.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 计算文件摘要
     *
     * @param md
     * @param file
     * @return
     */
    public static byte[] digest(MessageDigest md, File file) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = fis.read(buffer)) > 0) {
                md.update(buffer, 0, numRead);
            }
            return md.digest();
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(fis);
        }
    }

    /**
     * 获取MD5实例
     *
     * @return
     */
    public static MessageDigest md5() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 获取SHA实例
     *
     * @return
     */
    public static MessageDigest sha() {
        try {
            return MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionChecker.throwException(e);
        }
    }

}
