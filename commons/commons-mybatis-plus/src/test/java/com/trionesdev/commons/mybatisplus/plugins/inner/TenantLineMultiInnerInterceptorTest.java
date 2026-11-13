package com.trionesdev.commons.mybatisplus.plugins.inner;

import com.trionesdev.commons.mybatisplus.plugins.handler.TenantLineMultiHandler;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TenantLineMultiInnerInterceptorTest {

    private static TenantLineMultiInnerInterceptor interceptor(boolean multi, Expression tenantIds) {
        return new TenantLineMultiInnerInterceptor(new TenantLineMultiHandler() {
            @Override
            public Expression getTenantId() {
                return new StringValue("t1");
            }

            @Override
            public boolean enableTenantMulti() {
                return multi;
            }

            @Override
            public Expression getTenantIds() {
                return tenantIds;
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return "t_ignored".equals(tableName);
            }
        });
    }

    private static ExpressionList<StringValue> ids(String... values) {
        return new ExpressionList<>(List.of(values).stream().map(StringValue::new).toList());
    }

    @Test
    void plainExpressionListIsParenthesized() {
        String sql = interceptor(true, ids("t1", "t2")).parserSingle("select * from t_user", null);
        assertEquals("SELECT * FROM t_user WHERE tenant_id IN ('t1', 't2')", sql);
    }

    @Test
    void singleTenantIdUsesEquals() {
        String sql = interceptor(true, ids("t9")).parserSingle("select * from t_user", null);
        assertEquals("SELECT * FROM t_user WHERE tenant_id = 't9'", sql);
    }

    @Test
    void multiDisabledUsesTenantId() {
        String sql = interceptor(false, ids("t1", "t2")).parserSingle("select * from t_user", null);
        assertEquals("SELECT * FROM t_user WHERE tenant_id = 't1'", sql);
    }

    @Test
    void subQueryIsParenthesized() throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse("select id from t_tenant");
        String sql = interceptor(true, select).parserSingle("select * from t_user", null);
        assertEquals("SELECT * FROM t_user WHERE tenant_id IN (SELECT id FROM t_tenant)", sql);
    }

    @Test
    void updateAndDeleteUseInCondition() {
        TenantLineMultiInnerInterceptor interceptor = interceptor(true, ids("t1", "t2"));
        assertEquals("UPDATE t_user SET name = 'a' WHERE id = 1 AND tenant_id IN ('t1', 't2')",
                interceptor.parserSingle("update t_user set name = 'a' where id = 1", null));
        assertEquals("DELETE FROM t_user WHERE id = 1 AND tenant_id IN ('t1', 't2')",
                interceptor.parserSingle("delete from t_user where id = 1", null));
    }

    @Test
    void insertUsesSingleTenantId() {
        String sql = interceptor(true, ids("t1", "t2")).parserSingle("insert into t_user (id) values (1)", null);
        assertEquals("INSERT INTO t_user (id, tenant_id) VALUES (1, 't1')", sql);
    }

    @Test
    void ignoredTableIsUntouched() {
        String sql = interceptor(true, ids("t1", "t2")).parserSingle("select * from t_ignored", null);
        assertEquals("SELECT * FROM t_ignored", sql);
    }

    @Test
    void propertiesConfiguredHandlerAlsoDrivesInsert() {
        TenantLineMultiInnerInterceptor interceptor = new TenantLineMultiInnerInterceptor();
        interceptor.setTenantLineMultiHandler(interceptor(true, ids("t1", "t2")).getTenantLineMultiHandler());
        assertEquals("INSERT INTO t_user (id, tenant_id) VALUES (1, 't1')",
                interceptor.parserSingle("insert into t_user (id) values (1)", null));
    }
}
