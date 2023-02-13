package com.moensun.commons.mybatisplus.plugins.inner;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.BaseMultiTableInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.PropertyMapper;
import com.moensun.commons.mybatisplus.plugins.handler.TenantLineMultiHandler;
import lombok.*;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TenantLineMultiInnerInterceptor extends BaseMultiTableInnerInterceptor implements InnerInterceptor {
    private TenantLineMultiHandler tenantLineMultiHandler;

    @Override
    public void beforeQuery(
            Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        if (InterceptorIgnoreHelper.willIgnoreTenantLine(ms.getId())) return;
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(parserSingle(mpBs.sql(), null));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreTenantLine(ms.getId())) return;
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(parserMulti(mpBs.sql(), null));
        }
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        String whereSegment = (String) obj;
        this.processSelectBody(select.getSelectBody(), whereSegment);
        List<WithItem> withItemsList = select.getWithItemsList();
        if (!CollectionUtils.isEmpty(withItemsList)) {
            withItemsList.forEach(withItem -> processSelectBody(withItem, whereSegment));
        }
    }

    @Override
    protected void processInsert(Insert insert, int index, String sql, Object obj) {
        if (!this.tenantLineMultiHandler.ignoreTable(insert.getTable().getName())) {
            List<Column> columns = insert.getColumns();
            if (!CollectionUtils.isEmpty(columns)) {
                String tenantIdColumn = this.tenantLineMultiHandler.getTenantIdColumn();
                if (!this.tenantLineMultiHandler.ignoreInsert(columns, tenantIdColumn)) {
                    columns.add(new Column(tenantIdColumn));
                    List<Expression> duplicateUpdateColumns = insert.getDuplicateUpdateExpressionList();
                    if (CollectionUtils.isNotEmpty(duplicateUpdateColumns)) {
                        EqualsTo equalsTo = new EqualsTo();
                        equalsTo.setLeftExpression(new StringValue(tenantIdColumn));
                        equalsTo.setRightExpression(this.tenantLineMultiHandler.getTenantId());
                        duplicateUpdateColumns.add(equalsTo);
                    }

                    Select select = insert.getSelect();
                    if (select != null) {
                        this.processInsertSelect(select.getSelectBody(), (String)obj);
                    } else {
                        if (insert.getItemsList() == null) {
                            throw ExceptionUtils.mpe("Failed to process multiple-table update, please exclude the tableName or statementId", new Object[0]);
                        }

                        ItemsList itemsList = insert.getItemsList();
                        Expression tenantId = this.tenantLineMultiHandler.getTenantId();
                        if (itemsList instanceof MultiExpressionList) {
                            ((MultiExpressionList)itemsList).getExpressionLists().forEach((el) -> {
                                el.getExpressions().add(tenantId);
                            });
                        } else {
                            ((ExpressionList)itemsList).getExpressions().add(tenantId);
                        }
                    }

                }
            }
        }
    }

    /**
     * update 语句处理
     */
    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        final Table table = update.getTable();
        if (tenantLineMultiHandler.ignoreTable(table.getName())) {
            // 过滤退出执行
            return;
        }
        update.setWhere(this.andExpression(table, update.getWhere()));
    }

    /**
     * delete 语句处理
     */
    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {
        if (tenantLineMultiHandler.ignoreTable(delete.getTable().getName())) {
            // 过滤退出执行
            return;
        }
        delete.setWhere(this.andExpression(delete.getTable(), delete.getWhere()));
    }

    /**
     * delete update 语句 where 处理
     */
    protected Expression andExpression(Table table, Expression where) {
        if (isMultiTenant(tenantLineMultiHandler.getTenantIds())) {
            //获得where条件表达式
            InExpression inExpression = new InExpression();
            inExpression.setLeftExpression(this.getAliasColumn(table));
            inExpression.setRightItemsList(tenantLineMultiHandler.getTenantIds());
            if (null != where) {
                if (where instanceof OrExpression) {
                    return new AndExpression(inExpression, new Parenthesis(where));
                } else {
                    return new AndExpression(inExpression, where);
                }
            }
            return inExpression;
        } else {
            //获得where条件表达式
            EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(this.getAliasColumn(table));
            equalsTo.setRightExpression(tenantLineMultiHandler.getTenantId());
            if (null != where) {
                if (where instanceof OrExpression) {
                    return new AndExpression(equalsTo, new Parenthesis(where));
                } else {
                    return new AndExpression(equalsTo, where);
                }
            }
            return equalsTo;
        }
    }

    /**
     * 处理 insert into select
     * <p>
     * 进入这里表示需要 insert 的表启用了多租户,则 select 的表都启动了
     *
     * @param selectBody SelectBody
     */
    protected void processInsertSelect(SelectBody selectBody, final String whereSegment) {
        PlainSelect plainSelect = (PlainSelect) selectBody;
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            // fixed gitee pulls/141 duplicate update
            processPlainSelect(plainSelect, whereSegment);
            appendSelectItem(plainSelect.getSelectItems());
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            appendSelectItem(plainSelect.getSelectItems());
            processInsertSelect(subSelect.getSelectBody(), whereSegment);
        }
    }

    /**
     * 追加 SelectItem
     *
     * @param selectItems SelectItem
     */
    protected void appendSelectItem(List<SelectItem> selectItems) {
        if (CollectionUtils.isEmpty(selectItems)) return;
        if (selectItems.size() == 1) {
            SelectItem item = selectItems.get(0);
            if (item instanceof AllColumns || item instanceof AllTableColumns) return;
        }
        selectItems.add(new SelectExpressionItem(new Column(tenantLineMultiHandler.getTenantIdColumn())));
    }

    /**
     * 租户字段别名设置
     * <p>tenantId 或 tableAlias.tenantId</p>
     *
     * @param table 表对象
     * @return 字段
     */
    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName()).append(StringPool.DOT);
        }
        column.append(tenantLineMultiHandler.getTenantIdColumn());
        return new Column(column.toString());
    }

    @Override
    public void setProperties(Properties properties) {
        PropertyMapper.newInstance(properties).whenNotBlank("tenantLineValuesHandler", ClassUtils::newInstance, this::setTenantLineMultiHandler);
    }


    private boolean isMultiTenant(ItemsList itemsList) {
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
            InExpression inExpression = new InExpression();
            inExpression.setLeftExpression(this.getAliasColumn(table));
            inExpression.setRightItemsList(tenantLineMultiHandler.getTenantIds());
            if (where == null) {
                return inExpression;
            }
            if (where instanceof OrExpression) {
                return new AndExpression(new Parenthesis(where), inExpression);
            } else {
                return new AndExpression(where, inExpression);
            }
        } else {
            EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(this.getAliasColumn(table));
            equalsTo.setRightExpression(tenantLineMultiHandler.getTenantId());
            if (where == null) {
                return equalsTo;
            }
            if (where instanceof OrExpression) {
                return new AndExpression(new Parenthesis(where), equalsTo);
            } else {
                return new AndExpression(where, equalsTo);
            }
        }
    }
}
