package com.unvus.query;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.mybatis.dynamic.sql.SqlColumn;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Projection {

    private List<SqlColumn> columns;
    private Map<NvSqlTable, BidiMap<SqlColumn, String>> tableColumnMap;

    private Projection() {
        this.columns = new ArrayList<>();
        this.tableColumnMap = new HashMap<>();
    }

    public static Projection of(SqlColumn... columns) {
        return Projection.of(Arrays.asList(columns));
    }

    public static Projection of(List<SqlColumn> columns) {
        Projection projection = new Projection();
        projection.add(columns);
        return projection;
    }

    public SqlColumn[] columns() {
        return (SqlColumn[])this.columns.toArray();
    }

    public Projection add(List<SqlColumn> columns) {
        this.columns.addAll(columns);
        return this;
    }

    public void merge(Object obj, Map<String, Object> map) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for(SqlColumn column: columns) {
            NvSqlTable table = (NvSqlTable)column.table();

            if(!tableColumnMap.containsKey(table)) {
                tableColumnMap.put(table, ((DualHashBidiMap)table.getFieldMap()).inverseBidiMap());
            }

            BidiMap<SqlColumn, String> columnMap = tableColumnMap.get(table);
            PropertyUtils.setProperty(obj, columnMap.get(column), map.get(column.name()));
        }
    }
}
