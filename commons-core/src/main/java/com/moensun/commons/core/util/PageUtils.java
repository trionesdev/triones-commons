package com.moensun.commons.core.util;

import com.moensun.commons.core.page.PageInfo;

import java.util.List;

public class PageUtils {

    public static <T, E> PageInfo<E> of(PageInfo<T> pageInfo, List<E> data) {
        PageInfo<E> page = new PageInfo<>();
        page.setPageNum(pageInfo.getPageNum());
        page.setPageSize(pageInfo.getPageSize());
        page.setPages(pageInfo.getPages());
        page.setTotal(pageInfo.getTotal());
        page.setRows(data);
        return page;
    }

    public static <E> PageInfo<E> of(List<E> data) {
        PageInfo<E> page = new PageInfo<>();
        page.setRows(data);
        return page;
    }

    public static <E> PageInfo<E> empty() {
        return new PageInfo<>();
    }

}
