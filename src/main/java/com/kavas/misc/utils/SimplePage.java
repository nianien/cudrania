package com.kavas.misc.utils;


import java.util.Collections;
import java.util.List;

/**
 * 简单分页对象
 *
 * @author skyfalling
 */

public class SimplePage<T> implements Page<T> {

    protected int pageNo = 1;
    protected int pageSize = 20;
    protected long totalCount = 0;
    protected boolean autoCount = false;
    protected List<T> result = Collections.emptyList();

    public SimplePage() {
    }

    public SimplePage(int pageSize, int pageNo) {
        this(pageSize, pageNo, 0L);
    }

    public SimplePage(int pageSize, int pageNo, long totalCount) {
        setPageSize(pageSize);
        setPageNo(pageNo);
        setTotalCount(totalCount);
    }


    /**
     * 获得当前页的页号
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页的页号,只能设置正数
     */
    public void setPageNo(int pageNo) {
        if (pageNo > 0) {
            this.pageNo = pageNo;
        }
    }

    /**
     * 获得每页的记录数量.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页的记录数量
     */
    public void setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }
    }

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置
     */
    public int getFirstIndex() {
        return (pageNo - 1) * pageSize + 1;
    }

    /**
     * 获取当前页的数据
     */
    public List<T> getResult() {
        return result;
    }

    /**
     * 设置当前页的数据
     */
    public void setResult(List<T> result) {
        this.result = result;
    }

    /**
     * 取得总记录数
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总记录数
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 是否自动计算总数
     *
     * @return
     */
    public boolean isAutoCount() {
        return autoCount;
    }

    /**
     * 设置自动计算总数
     *
     * @param autoCount
     */
    public void setAutoCount(boolean autoCount) {
        this.autoCount = autoCount;
    }

    /**
     * 根据pageSize与totalCount计算总页数
     */
    public long getTotalPages() {
        long count = totalCount / pageSize;
        if (totalCount % pageSize > 0) {
            count++;
        }
        return count;
    }

}
