package com.moensun.commons.mybatisplus.plugins.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;

/**
 * 允许同时查询多个租户的多租户
 */
public interface TenantLineMultiHandler extends TenantLineHandler {
    /**
     * 是否开启多租户查询 支持查询多个租户数据
     * 如果不开启，则与普通多租户一样
     * @return
     */
    boolean enableTenantMulti();

    ItemsList getTenantIds();

}
