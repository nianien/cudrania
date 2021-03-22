package com.cudrania.core.io;

import com.cudrania.core.exception.ExceptionChecker;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * 支持对象序列化和反序列化的工具类
 *
 * @author skyfalling
 */
public class ObjectSerializer {

    /**
     * 向OutputStream对象写入可序列化实例
     *
     * @param object
     * @param out
     */
    public static void write(Serializable object, OutputStream out) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(out);
            oos.writeObject(object);
        } catch (Exception e) {
            ExceptionChecker.throwException(e);
        } finally {
            Closer.close(oos);
        }
    }

    /**
     * 将可序列化对象写入指定文件
     *
     * @param object
     * @param path
     */
    public static void write(Serializable object, String path) {
        try {
            // 读写隐藏文件必须使用RandomAccessFile对象
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            // 清空文件内容
            raf.setLength(0);
            // 获取文件描述符，用来创建文件输入流
            write(object, new FileOutputStream(raf.getFD()));
        } catch (Exception e) {
            ExceptionChecker.throwException(e);
        }
    }

    /**
     * 将可序列化对象转换为字节数组
     *
     * @param object
     * @return
     */
    public static byte[] writeBytes(Serializable object) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(object, out);
        return out.toByteArray();
    }

    /**
     * 读取InputStream对象中的序列化实例
     *
     * @param in
     * @return
     */
    public static Object read(InputStream in) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(in);
            Object obj = ois.readObject();
            return obj;
        } catch (Exception e) {
            return ExceptionChecker.throwException(e);
        } finally {
            Closer.close(ois);
        }
    }

    /**
     * 从指定文件中读取序列化实例对象
     *
     * @param path
     * @return
     */
    public static Object read(String path) {
        try {
            // 读写隐藏文件必须使用RandomAccessFile对象
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            return read(new FileInputStream(raf.getFD()));
        } catch (Exception e) {
            return ExceptionChecker.throwException(e);
        }
    }

    /**
     * 将字节数组转化为可序列化的实例对象
     *
     * @param bytes
     * @return
     */
    public static Object readObject(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            return read(new BufferedInputStream(bis));
        } catch (Exception e) {
            return ExceptionChecker.throwException(e);
        }
    }
}
