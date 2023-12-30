package com.trionesdev.commons.mybatisplus.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trionesdev.commons.core.page.PageCriteria;
import com.trionesdev.commons.core.page.PageInfo;
import com.trionesdev.commons.core.util.PageUtils;

import java.util.List;

public class MpPageUtils extends PageUtils {
    public static <T> PageInfo<T> of(IPage<T> pageInfo) {
        PageInfo<T> page = new PageInfo<>();
        page.setPageNum((int) pageInfo.getCurrent());
        page.setPageSize((int) pageInfo.getSize());
        page.setPages((int) pageInfo.getPages());
        page.setTotal(pageInfo.getTotal());
        page.setRows(pageInfo.getRecords());
        return page;
    }

    public static <T, E> PageInfo<E> of(IPage<T> pageInfo, List<E> data) {
        PageInfo<E> page = new PageInfo<>();
        page.setPageNum((int) pageInfo.getCurrent());
        page.setPageSize((int) pageInfo.getSize());
        page.setPages((int) pageInfo.getPages());
        page.setTotal(pageInfo.getTotal());
        page.setRows(data);
        return page;
    }

    public static <T> IPage<T> page(PageCriteria criteria) {
        return new Page<>(criteria.getPageNum(), criteria.getPageSize());
    }
}
