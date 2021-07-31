package com.moensun.commons.core.page;

import java.util.List;

import org.springframework.data.domain.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;

public class PageUtils {

    public static <T> MSPageInfo<T> of(PageInfo<T> pageInfo) {
        MSPageInfo<T> page = new MSPageInfo<>();
        page.setPageNum(pageInfo.getPageNum());
        page.setPageSize(pageInfo.getPageSize());
        page.setPages(pageInfo.getPages());
        page.setTotal(pageInfo.getTotal());
        page.setRows(pageInfo.getList());
        return page;
    }

    public static <T, E> MSPageInfo<E> of(PageInfo<T> pageInfo, List<E> data) {
        MSPageInfo<E> page = new MSPageInfo<>();
        page.setPageNum(pageInfo.getPageNum());
        page.setPageSize(pageInfo.getPageSize());
        page.setPages(pageInfo.getPages());
        page.setTotal(pageInfo.getTotal());
        page.setRows(data);
        return page;
    }

    public static <T> MSPageInfo<T> of(IPage<T> pageInfo) {
        MSPageInfo<T> page = new MSPageInfo<>();
        page.setPageNum((int) pageInfo.getCurrent());
        page.setPageSize((int) pageInfo.getSize());
        page.setPages((int) pageInfo.getPages());
        page.setTotal(pageInfo.getTotal());
        page.setRows(pageInfo.getRecords());
        return page;
    }

    public static <T> MSPageInfo<T> of(Page<T> pageInfo) {
        MSPageInfo<T> page = new MSPageInfo<>();
        page.setPageNum(pageInfo.getNumber());
        page.setPageSize( pageInfo.getSize());
        page.setPages(pageInfo.getTotalPages());
        page.setTotal(pageInfo.getTotalElements());
        page.setRows(pageInfo.getContent());
        return page;
    }

    public static <T, E> MSPageInfo<E> of(MSPageInfo<T> pageInfo, List<E> data) {
        MSPageInfo<E> page = new MSPageInfo<>();
        page.setPageNum(pageInfo.getPageNum());
        page.setPageSize(pageInfo.getPageSize());
        page.setPages(pageInfo.getPages());
        page.setTotal(pageInfo.getTotal());
        page.setRows(data);
        return page;
    }

    public static <E> MSPageInfo<E> of(List<E> data){
        MSPageInfo<E> page = new MSPageInfo<>();
        page.setRows(data);
        return page;
    }

    public static <E> MSPageInfo<E> empty(){
        return new MSPageInfo<>();
    }

}
