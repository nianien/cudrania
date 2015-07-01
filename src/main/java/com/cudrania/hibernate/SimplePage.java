package com.cudrania.hibernate;

import java.util.Collections;
import java.util.List;

/**
 * 分页查询对象接口定义
 *
 * @author skyfalling
 */

/**
 * 分页查询对象接口定义
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

    /**
     * 构造方法
     *
     * @param pageSize
     * @param pageNo
     */
    public SimplePage(int pageSize, int pageNo) {
        this(pageSize, pageNo, 0L);
    }

    /**
     * 构造方法
     *
     * @param pageSize
     * @param pageNo
     * @param totalCount
     */
    public SimplePage(int pageSize, int pageNo, long totalCount) {
        setPageSize(pageSize);
        setPageNo(pageNo);
        setTotalCount(totalCount);
    }

    /**
     * 获得当前页的页号
     */
    @Override
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页的页号,只能设置正数
     */
    @Override
    public void setPageNo(int pageNo) {
        if (pageNo > 0) {
            this.pageNo = pageNo;
        }
    }

    /**
     * 获得每页的记录数量.
     */
    @Override
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页的记录数量
     */
    @Override
    public void setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }
    }

    /**
     * 获取当前页的数据
     */
    @Override
    public List<T> getResult() {
        return result;
    }

    /**
     * 设置当前页的数据
     */
    @Override
    public void setResult(List<T> result) {
        this.result = result;
    }

    /**
     * 取得总记录数
     */
    @Override
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总记录数
     */
    @Override
    public void setTotalCount(long totalCount) {
        if (totalCount > 0) {
            this.totalCount = totalCount;
        }
    }


    /**
     * 是否自动计算总数
     *
     * @return
     */
    @Override
    public boolean isAutoCount() {
        return autoCount;
    }

    /**
     * 设置自动计算总数
     *
     * @param autoCount
     */
    @Override
    public void setAutoCount(boolean autoCount) {
        this.autoCount = autoCount;
    }

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置
     */
    @Override
    public int getFirst() {
        return (pageNo - 1) * pageSize + 1;
    }

    /**
     * 根据pageSize与totalCount计算总页数
     */
    @Override
    public int getTotalPages() {
        int count = (int) (totalCount / pageSize);
        if (totalCount % pageSize > 0) {
            count++;
        }
        return count;
    }

    /**
     * 是否有下一页.
     */
    @Override
    public boolean isHasNext() {
        return pageNo < getTotalPages();
    }

    /**
     * 取得下页的页号, 序号从1开始.
     * 当前页为尾页时仍返回尾页序号.
     */
    @Override
    public int getNextPage() {
        return isHasNext() ? pageNo + 1 : pageNo;
    }

    /**
     * 是否有上一页.
     */
    @Override
    public boolean isHasPre() {
        return (pageNo - 1 >= 1);
    }

    /**
     * 取得上页的页号, 序号从1开始.
     * 当前页为首页时返回首页序号.
     */
    @Override
    public int getPrePage() {
        return isHasPre() ? pageNo - 1 : pageNo;
    }
}
