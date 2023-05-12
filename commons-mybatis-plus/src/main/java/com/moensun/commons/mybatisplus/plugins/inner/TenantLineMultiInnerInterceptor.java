package com.moensun.commons.mybatisplus.plugins.inner;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.PropertyMapper;
import com.moensun.commons.mybatisplus.plugins.handler.TenantLineMultiHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.WithItem;

import java.util.List;
import java.util.Properties;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TenantLineMultiInnerInterceptor extends TenantLineInnerInterceptor implements InnerInterceptor {
    private TenantLineMultiHandler tenantLineMultiHandler;

    public TenantLineMultiInnerInterceptor(TenantLineMultiHandler tenantLineMultiHandler) {
        super(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return tenantLineMultiHandler.getTenantId();
            }

            @Override
            public String getTenantIdColumn() {
                return tenantLineMultiHandler.getTenantIdColumn();
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return tenantLineMultiHandler.ignoreTable(tableName);
            }

            @Override
            public boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
                return tenantLineMultiHandler.ignoreInsert(columns, tenantIdColumn);
            }
        });
        this.tenantLineMultiHandler = tenantLineMultiHandler;
    }

    @Override
    public void setProperties(Properties properties) {
        PropertyMapper.newInstance(properties).whenNotBlank("tenantLineValuesHandler", ClassUtils::newInstance, this::setTenantLineMultiHandler);
    }


    private boolean isMultiTenant(ItemsList itemsList) {
        if (!tenantLineMultiHandler.enableTenantMulti()) {
            return false;
        }
        if (itemsList instanceof ExpressionList) {
            List<Expression> expressionList = ((ExpressionList) itemsList).getExpressions();
            return CollectionUtils.isNotEmpty(expressionList) && expressionList.size() > 1;
        } else if (itemsList instanceof MultiExpressionList) {
            List<ExpressionList> expressionLists = ((MultiExpressionList) itemsList).getExpressionLists();
            return CollectionUtils.isNotEmpty(expressionLists) && expressionLists.get(0).getExpressions().size() > 1;
        } else if (itemsList instanceof NamedExpressionList) {
            List<Expression> expressionList = ((NamedExpressionList) itemsList).getExpressions();
            return CollectionUtils.isNotEmpty(expressionList) && expressionList.size() > 1;
        } else if (itemsList instanceof SubSelect) {
            List<WithItem> withItems = ((SubSelect) itemsList).getWithItemsList();
            return CollectionUtils.isNotEmpty(withItems) && withItems.size() > 1;
        }
        return false;
    }

    @Override
    public Expression buildTableExpression(Table table, Expression where, String whereSegment) {
        if (tenantLineMultiHandler.ignoreTable(table.getName())) {
            return null;
        }
        if (isMultiTenant(tenantLineMultiHandler.getTenantIds())) {
            return new InExpression(getAliasColumn(table), tenantLineMultiHandler.getTenantIds());
        } else {
            return new EqualsTo(getAliasColumn(table), tenantLineMultiHandler.getTenantId());
        }
    }
}
