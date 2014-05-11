package com.kavas.misc.utils;

import java.util.List;

/**
 * 分页查询对象接口定义
 *
 * @author skyfalling
 */
public interface Page<T> {

    public int getPageNo();

    public void setPageNo(int pageNo);

    public int getPageSize();

    public void setPageSize(int pageSize);

    public List<T> getResult();

    public void setResult(List<T> result);

}
