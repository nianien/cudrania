package com.cudrania.hibernate;

import java.util.List;

/**
 * 分页接口定义
 *
 * @author skyfalling
 */
public interface Page<T> {
    /**
     * 获取当前页码
     *
     * @return
     */
    int getPageNo();

    /**
     * 设置当前页码
     *
     * @param pageNo
     */
    void setPageNo(int pageNo);

    /**
     * 获取当前页记录数
     *
     * @return
     */
    int getPageSize();

    /**
     * 设置当前页记录数
     *
     * @param pageSize
     */
    void setPageSize(int pageSize);

    /**
     * 获取当前页对象内容
     *
     * @return
     */
    List<T> getResult();

    /**
     * 设置当前页对象内容
     *
     * @param result
     */
    void setResult(List<T> result);

    /**
     * 获取记录总数
     *
     * @return
     */
    long getTotalCount();

    /**
     * 设置记录总数
     *
     * @param totalCount
     */
    void setTotalCount(long totalCount);

    /**
     * 标记是否计算总数
     *
     * @return
     */
    boolean isAutoCount();

    /**
     * 标记是否计算总数
     *
     * @param autoCount
     */
    void setAutoCount(boolean autoCount);

    /**
     * 获取当前页第一条记录在总计录中的位置
     *
     * @return
     */
    int getFirst();

    /**
     * 获取页面总数
     *
     * @return
     */
    long getTotalPages();

    /**
     * 是否有下一页
     *
     * @return
     */
    boolean isHasNext();

    /**
     * 下一页页码
     *
     * @return
     */
    int getNextPage();

    /**
     * 是否有上一页
     *
     * @return
     */
    boolean isHasPre();

    /**
     * 上一页页码
     *
     * @return
     */
    int getPrePage();
}
