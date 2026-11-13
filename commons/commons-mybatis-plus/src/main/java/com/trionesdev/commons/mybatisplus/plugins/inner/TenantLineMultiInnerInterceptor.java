package com.trionesdev.commons.mybatisplus.plugins.inner;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.PropertyMapper;
import com.trionesdev.commons.mybatisplus.plugins.handler.TenantLineMultiHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.List;
import java.util.Properties;

/**
 * 支持同时查询多个租户的多租户插件。
 * <p>
 * {@link TenantLineMultiHandler#enableTenantMulti()} 为 true 时，按 {@link TenantLineMultiHandler#getTenantIds()} 生成条件：
 * <ul>
 *     <li>多个值的 {@link ExpressionList}：{@code tenant_id IN (a, b)}，无需调用方自行加括号</li>
 *     <li>只有一个值的 {@link ExpressionList}：{@code tenant_id = a}</li>
 *     <li>子查询 {@link Select}：{@code tenant_id IN (SELECT ...)}</li>
 * </ul>
 * 其他情况与 {@link TenantLineInnerInterceptor} 一致，使用 {@code tenant_id = getTenantId()}。插入语句始终使用 {@code getTenantId()}。
 */
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TenantLineMultiInnerInterceptor extends TenantLineInnerInterceptor implements InnerInterceptor {
    private TenantLineMultiHandler tenantLineMultiHandler;

    public TenantLineMultiInnerInterceptor(TenantLineMultiHandler tenantLineMultiHandler) {
        setTenantLineMultiHandler(tenantLineMultiHandler);
    }

    /**
     * 同时设置父类使用的 {@link TenantLineHandler}，保证插入、忽略表等逻辑与查询条件一致。
     */
    public void setTenantLineMultiHandler(TenantLineMultiHandler tenantLineMultiHandler) {
        this.tenantLineMultiHandler = tenantLineMultiHandler;
        super.setTenantLineHandler(tenantLineMultiHandler == null ? null : adapt(tenantLineMultiHandler));
    }

    @Override
    public void setProperties(Properties properties) {
        PropertyMapper.newInstance(properties).whenNotBlank("tenantLineValuesHandler", ClassUtils::newInstance, this::setTenantLineMultiHandler);
    }

    @Override
    public Expression buildTableExpression(Table table, Expression where, String whereSegment) {
        if (tenantLineMultiHandler.ignoreTable(table.getName())) {
            return null;
        }
        Column column = getAliasColumn(table);
        if (tenantLineMultiHandler.enableTenantMulti()) {
            Expression tenantIds = tenantLineMultiHandler.getTenantIds();
            if (tenantIds instanceof ExpressionList<?> list && !list.isEmpty()) {
                if (list.size() == 1) {
                    return new EqualsTo(column, (Expression) list.get(0));
                }
                return new InExpression(column, parenthesize(list));
            }
            if (tenantIds instanceof Select select) {
                return new InExpression(column, parenthesize(select));
            }
        }
        return new EqualsTo(column, tenantLineMultiHandler.getTenantId());
    }

    /**
     * 普通 {@link ExpressionList} 在 JSqlParser 4.7+ 中输出时不带括号，会生成非法的 {@code IN a, b}。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Expression parenthesize(ExpressionList<?> list) {
        return list instanceof ParenthesedExpressionList ? list : new ParenthesedExpressionList(list);
    }

    private static Expression parenthesize(Select select) {
        return select instanceof ParenthesedSelect ? select : new ParenthesedSelect().withSelect(select);
    }

    private static TenantLineHandler adapt(TenantLineMultiHandler handler) {
        return new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return handler.getTenantId();
            }

            @Override
            public String getTenantIdColumn() {
                return handler.getTenantIdColumn();
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return handler.ignoreTable(tableName);
            }

            @Override
            public boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
                return handler.ignoreInsert(columns, tenantIdColumn);
            }
        };
    }
}
