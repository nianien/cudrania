package com.cudrania.test.files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.io.Closer;
import com.cudrania.core.io.Files;
import com.cudrania.core.loader.ResourceLoader;
import com.cudrania.core.utils.TimeCounter;

public class TestIo {
    private final static int bufferSize = 1024 * 8;

    @Test
    public void test() {
        File file = ResourceLoader.getFile("all200000.txt");// all200000.txt
        int time = 100;
        assert file.exists();
        TimeCounter tc = new TimeCounter();
        byte[] bytes = getBytes(file);
        tc.start();
        for (int i = 0; i < time; i++) {
            bytes = getBytes(file);
        }
        tc.stop();
        System.out.println("ByteArrayOutputStream time cost:" + tc.timePassed() + ",byte length:" + bytes.length);

        byte[] bytes2 = getBytes2(file);
        tc.start();
        for (int i = 0; i < time; i++) {
            bytes2 = getBytes2(file);
        }
        tc.stop();
        System.out.println("ByteArray of List time cost:" + tc.timePassed() + ",byte length:" + bytes2.length);
        byte[] bytes3 = Files.getBytes(file);
        tc.start();
        for (int i = 0; i < time; i++) {
            bytes3 = Files.getBytes(file);
        }
        tc.stop();
        System.out.println("time cost:" + tc.timePassed() + ",byte length:" + bytes3.length);
        assert Arrays.equals(bytes, bytes2);
        assert Arrays.equals(bytes, bytes3);

    }

    public static byte[] getBytes(File file) {
        try {
            return getBytes(new FileInputStream(file));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    public static byte[] getBytes2(File file) {
        try {
            return getBytes2(new FileInputStream(file));
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 获取InputStream对象指定长度的字节内容
     *
     * @param inputStream
     * @return 文件字节内容
     */
    public static byte[] getBytes(InputStream inputStream) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read;
            byte[] buffer = new byte[bufferSize];
            while ((read = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(inputStream);
        }
    }

    public static byte[] getBytes2(InputStream inputStream) {
        try {
            List<byte[]> buffers = new ArrayList<byte[]>();
            int size = 0;
            int read;
            do {
                byte[] buffer = new byte[bufferSize];
                read = inputStream.read(buffer);
                if (read > 0) {
                    size += read;
                    buffers.add(Arrays.copyOf(buffer, read));
                }
            } while (read == bufferSize);
            byte[] bytes = new byte[size];
            int position = 0;
            for (byte[] buffer : buffers) {
                System.arraycopy(buffer, 0, bytes, position, buffer.length);
                position += buffer.length;
            }
            return bytes;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(inputStream);
        }
    }
}
