package com.cudrania.algorithm;

import com.cudrania.core.io.Closer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 支持海量数据排序的工具类,支持对超大文件或数据源的排序,支持对多个有序文件或数据源的合并
 *
 * @author skyfalling
 */
public class MassSorter {

    /**
     * 用于进行字符串比较的对象,可以改变字符串的比较规则
     */
    private Comparator<String> comparator;

    /**
     * 一次可以加载的最大数据量
     */
    private int maxBufferSize;

    /**
     * 构造方法, 指定一次加载的最大数据量, 同时指定字符串的比较规则
     *
     * @param maxBufferSize 一次可以加载的最大数据量
     * @param comparator    指定字符串的比较规则
     */
    public MassSorter(int maxBufferSize, Comparator<String> comparator) {
        this.maxBufferSize = maxBufferSize / 2;
        this.comparator = comparator;
    }

    /**
     * 构造方法, 指定单次加载的数据量, 并使用字符串的默认比较规则
     *
     * @param maxBufferSize 一次可以加载的最大数据量
     */
    public MassSorter(int maxBufferSize) {
        this(maxBufferSize, (str1, str2) -> str1.compareTo(str1));
    }

    /**
     * 将文件srcFile内容排序写入到destFile中
     *
     * @param srcFile
     * @param destFile
     * @throws Exception
     */
    public void sort(File srcFile, File destFile) throws Exception {
        sort(new FileReader(srcFile), destFile);
    }

    /**
     * 读取reader对象的数据排序后写入到文件destFile中
     *
     * @param reader
     * @param destFile
     * @throws Exception
     */
    public void sort(Reader reader, File destFile) throws Exception {
        List<File> files = split(reader);
        try {
            while (files.size() > 1) {
                File file1 = files.remove(0);
                File file2 = files.remove(0);
                try {
                    File file3 = File.createTempFile("~.tmp", "");
                    sort(file1, file2, file3);
                    files.add(file3);
                } finally {
                    file1.delete();
                    file2.delete();
                }
            }
            if (files.size() > 0) {
                files.get(0).renameTo(destFile);
            }
        } finally {
            for (File file : files) {
                file.delete();
            }
        }
    }

    /**
     * 分段读取reader对象的数据并排序, 返回有序文件列表
     *
     * @param reader
     * @return
     * @throws Exception
     */
    private List<File> split(Reader reader) throws Exception {
        List<File> files = new ArrayList<File>();
        try {
            BufferedReader bufferReader = new BufferedReader(reader);
            do {
                List<String> data = new ArrayList<String>(maxBufferSize);
                read(data, bufferReader, maxBufferSize);
                if (data.isEmpty())
                    break;
                Collections.sort(data, comparator);
                File tmpFile = File.createTempFile("~.tmp", "");
                files.add(tmpFile);
                write(data, new BufferedWriter(new FileWriter(tmpFile)), true);
            } while (true);
        } catch (Exception e) {
            for (File file : files) {
                file.delete();
            }
            throw e;
        } finally {
            Closer.close(reader);
        }
        return files;
    }

    /**
     * 将文件firstFile和secondFile的内容排序后输出到destFile中<br>
     * 注意:这里要求文件firstFile和secondFile的内容是有序的
     *
     * @param firstFile
     * @param secondFile
     * @param destFile
     * @throws Exception
     */
    public void sort(File firstFile, File secondFile, File destFile)
            throws Exception {
        sort(new FileReader(firstFile), new FileReader(secondFile),
                new FileWriter(destFile));
    }

    /**
     * 从firstReader和secondReader对象中读取数据进行排序后写入writer对象<br>
     * 注意:这里要求从firstReader和secondReader对象读取的内容是有序的
     *
     * @param firstReader
     * @param secondReader
     * @param writer
     * @throws Exception
     */
    public void sort(Reader firstReader, Reader secondReader, Writer writer)
            throws Exception {
        try {
            BufferedReader bufferReader1 = new BufferedReader(firstReader);
            BufferedReader bufferReader2 = new BufferedReader(secondReader);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            List<String> first = new ArrayList<String>();
            List<String> second = new ArrayList<String>();
            do {
                read(first, bufferReader1, maxBufferSize);
                read(second, bufferReader2, maxBufferSize);
                if (first.isEmpty() && second.isEmpty())
                    break;
                if (first.isEmpty()) {
                    write(second, bufferWriter, false);
                    continue;
                }
                if (second.isEmpty()) {
                    write(first, bufferWriter, false);
                    continue;
                }
                while (!first.isEmpty() && !second.isEmpty()) {
                    if (comparator.compare(first.get(0), second.get(0)) < 0) {
                        bufferWriter.write(first.remove(0));
                    } else {
                        bufferWriter.write(second.remove(0));
                    }
                    bufferWriter.newLine();
                }
                bufferWriter.flush();
            } while (true);
        } finally {
            Closer.close(firstReader);
            Closer.close(secondReader);
            Closer.close(writer);
        }

    }

    /**
     * 从reader对象中读取数据填充到链表中,同时确保元素数目最大不超过maxSize
     *
     * @param list    数据链表
     * @param reader  读对象
     * @param maxSize 链表元素数目最大值
     * @throws Exception
     */
    private void read(List<String> list, BufferedReader reader, int maxSize)
            throws Exception {
        for (int i = list.size(); i < maxSize; i++) {
            String line = reader.readLine();
            if (line == null)
                break;
            list.add(line);
        }
    }

    /**
     * 将链表中的数据移除并写入writer对象
     *
     * @param list   数据链表
     * @param writer 写对象
     * @param close  是否关闭writer对象
     * @throws Exception
     */
    private void write(List<String> list, BufferedWriter writer, boolean close)
            throws Exception {
        try {
            while (!list.isEmpty()) {
                writer.write(list.remove(0));
                writer.newLine();
            }
            writer.flush();
        } finally {
            if (close) {
                Closer.close(writer);
            }

        }
    }
}
