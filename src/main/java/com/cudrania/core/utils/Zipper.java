package com.cudrania.core.utils;

import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.io.Closer;
import com.cudrania.core.io.FileSearcher;
import com.cudrania.core.io.Files;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 处理压缩和解压的工具类
 *
 * @author skyfalling
 */
public class Zipper {
    /**
     * 处理压缩文件名称的接口类
     *
     * @author skyfalling
     */
    public static interface EntryHandler {
        /**
         * 获取压缩后的文件名称
         *
         * @param file
         * @return 压缩包对应的文件名称
         */
        String getEntryName(File file);
    }

    private final static int bufferSize = 8192;

    /**
     * 压缩给定的文件列表,压缩后的文件在包根目录中
     *
     * @param files   待压缩的文件集合
     * @param zipPath 压缩后的zip文件
     */

    public static void zip(List<File> files, File zipPath) {
        zip(files, zipPath, file -> {
            // 待压缩文件的名称
            return file.getName();
        });
    }

    /**
     * 压缩指定的文件集合,并指定压缩后的文件名称
     *
     * @param filesMap 待压缩的文件映射集合,其中key值对应文件,value值对应压缩后的文件名
     * @param zipPath  压缩后的zip文件
     */

    public static void zip(final Map<File, String> filesMap, File zipPath) {
        // 压缩文件
        zip(filesMap.keySet(), zipPath, file -> {
            // 压缩文件的映射名称
            return filesMap.get(file);
        });
    }

    /**
     * 压缩指定目录到父目录,压缩文件与目录同名
     *
     * @param dir 待压缩的文件目录
     */
    public static void zip(File dir) {
        Zipper.zip(dir, new File(dir.getParentFile(), dir.getName() + ".zip"));
    }

    /**
     * 压缩指定目录下的文件,保持文件的目录结构
     *
     * @param dir     待压缩的文件目录
     * @param zipPath 压缩后的zip文件
     */
    public static void zip(File dir, File zipPath) {
        zip(dir, zipPath, pathname -> true);
    }

    /**
     * 压缩指定目录下的文件,保持文件的目录结构
     *
     * @param dir     待压缩的文件目录
     * @param zipPath 压缩后的zip文件
     * @param filter  文件选择器,选择符合条件的文件进行压缩
     */
    public static void zip(File dir, File zipPath, FileFilter filter) {
        final String dirPath = dir.getAbsolutePath();
        zip(FileSearcher.find(dir, filter), zipPath, file -> {
            String filePath = file.getAbsolutePath();
            int index = filePath.indexOf(dirPath);
            // 压缩文件的相对路径
            return filePath.substring(index + dirPath.length() + 1);
        });
    }

    /**
     * 压缩文件列表,并根据文件路径指定压缩后的文件名称
     *
     * @param files   待压缩的文件路径集合
     * @param zipPath 压缩后的zip文件
     * @param handler 压缩包中文件名称的处理接口
     */

    public static void zip(Iterable<File> files, File zipPath,
                           EntryHandler handler) {
        // 创建文件输出流对象
        Files.createParent(zipPath);
        try {
            zip(files, new FileOutputStream(zipPath), handler);
        } catch (Exception e) {
            // 如果压缩失败,将压缩文件删除
            if (zipPath != null && zipPath.exists()) {
                zipPath.delete();
            }
            ExceptionChecker.throwException(e);
        }
    }

    /**
     * 压缩文件列表,并根据文件路径指定压缩后的文件名称
     *
     * @param files   待压缩的文件路径集合
     * @param os      压缩后输出流对象
     * @param handler 压缩包中文件名称的处理接口
     */

    public static void zip(Iterable<File> files, OutputStream os,
                           EntryHandler handler) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new CheckedOutputStream(os, new CRC32()));
            // 8M缓冲区
            byte[] buffer = new byte[bufferSize];
            // 读取字节数
            int readed = 0;
            for (File file : files) {
                // 待压缩文件的名称
                String fileName = handler.getEntryName(file);
                if (file.isDirectory() && !fileName.endsWith("/")) {
                    fileName += "/";
                }
                // 将文件压缩到zip文件中
                zos.putNextEntry(new org.apache.tools.zip.ZipEntry(fileName));
                if (file.isFile()) {
                    // 写文件内容
                    FileInputStream in = new FileInputStream(file);
                    try {
                        while ((readed = in.read(buffer)) != -1) {
                            zos.write(buffer, 0, readed);
                        }
                    } finally {
                        zos.closeEntry();
                        Closer.close(in);
                    }
                }
            }
        } catch (Exception e) {
            ExceptionChecker.throwException(e);
        } finally {
            Closer.close(zos);
            Closer.close(os);
        }
    }

    /**
     * 解压Zip文件到当前目录
     *
     * @param zipPath
     */
    public static void unzip(File zipPath) {
        Zipper.unzip(
                zipPath,
                new File(zipPath.getParentFile(), Files
                        .getFileNameWithoutExt(zipPath.getName())), false);
    }

    /**
     * 解压Zip文件到指定目录,如果是相对路径,则表示相对于压缩文件所在目录
     *
     * @param zipPath   压缩文件
     * @param destDir   解压目录
     * @param deleteZip 是否删除压缩文件
     */
    public static void unzip(File zipPath, String destDir, boolean deleteZip) {
        File file = new File(destDir);
        // 如果解压路径不是绝对路径,则表示相对于压缩包所在目录
        File dir = file.isAbsolute() ? file : new File(zipPath.getParent(),
                destDir);
        unzip(zipPath, dir, deleteZip);
    }

    /**
     * 解压Zip文件到指定目录
     *
     * @param zipPath   压缩文件
     * @param destDir   解压目录
     * @param deleteZip 是否删除压缩文件
     */
    public static void unzip(File zipPath, File destDir, boolean deleteZip) {
        ExceptionChecker.throwIf(!zipPath.exists(), "指定的压缩文件不存在："
                + zipPath);
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipPath);
            Enumeration<?> e = zipFile.getEntries();
            while (e.hasMoreElements()) {
                org.apache.tools.zip.ZipEntry zipEntry = (org.apache.tools.zip.ZipEntry) e
                        .nextElement();
                String name = zipEntry.getName();
                File file = new File(destDir, name);
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                    continue;
                } else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(file);
                Files.write(fos, is);
            }
            if (deleteZip) {
                try {
                    zipPath.delete();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            ExceptionChecker.throwException(e);
        } finally {
            Closer.close(zipFile);
        }
    }

    /**
     * 按字节数组读取压缩包指定文件的内容,文件大小不能超过int型的上限
     *
     * @param zipPath
     * @param fileName
     * @return 压缩包中文件的字节内容
     */
    public static byte[] getBytes(String zipPath, String fileName) {
        return Files.getBytes(getZipInputStream(zipPath, fileName));

    }

    /**
     * 读取压缩包中指定文件大小,文件大小不能超过int型的上限
     *
     * @param zipPath
     * @param fileName
     * @return 压缩包中文件的字节大小
     */
    public static int getSize(String zipPath, String fileName) {
        ZipInputStream zin = getZipInputStream(zipPath, fileName);
        try {
            int len = 0;
            int readed = 0;
            byte[] buffer = new byte[bufferSize];
            while ((readed = zin.read(buffer)) != -1) {
                len += readed;
            }
            return len;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        } finally {
            Closer.close(zin);
        }
    }

    /**
     * 按字符读取压缩包指定文件的内容
     *
     * @param zipPath
     * @param fileName
     * @return 压缩包中文件的文本内容
     */
    public static String getString(String zipPath, String fileName) {
        return Files.read(getZipInputStream(zipPath, fileName));
    }

    /**
     * 读取压缩包中指定文件的内容
     *
     * @param zipPath
     * @param fileName
     * @return 压缩包中文件的文本内容
     */
    public static String getString(String zipPath, String fileName,
                                   String charset) throws Exception {
        return Files.read(getZipInputStream(zipPath, fileName), charset);
    }

    /**
     * 按指定编码读取输入流中的内容
     *
     * @param zin
     * @param charset
     * @return 输入流中的文本内容
     */
    public static String getString(ZipInputStream zin, String charset) throws Exception {
        return Files.read(zin, charset);
    }

    /**
     * 获取压缩包指定文件的输入流
     *
     * @param zipPath
     * @param fileName
     * @return 压缩包中文件的字节内容
     */
    public static ZipInputStream getZipInputStream(String zipPath,
                                                   String fileName) {
        try {
            ZipInputStream zin = new ZipInputStream(
                    new FileInputStream(zipPath));
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.getName().equals(fileName))
                    break;
            }
            return zin;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

}
