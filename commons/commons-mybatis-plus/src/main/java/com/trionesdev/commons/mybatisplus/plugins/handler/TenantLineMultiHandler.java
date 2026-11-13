package com.trionesdev.commons.mybatisplus.plugins.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;

/**
 * 允许同时查询多个租户的多租户
 */
public interface TenantLineMultiHandler extends TenantLineHandler {
    /**
     * 是否开启多租户查询 支持查询多个租户数据
     * 如果不开启，则与普通多租户一样
     *
     * @return boolean
     */
    default boolean enableTenantMulti() {
        return false;
    }

    /**
     * 多租户查询时的租户 ID 集合。返回 {@code ExpressionList}（例如 {@code new ExpressionList<>(new StringValue("t1"), ...)}，
     * 无需自行加括号）或子查询 {@code Select}。
     */
    Expression getTenantIds();

}
