package com.moensun.commons.mybatisplus.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moensun.commons.core.page.PageInfo;
import com.moensun.commons.core.util.PageUtils;

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
}
