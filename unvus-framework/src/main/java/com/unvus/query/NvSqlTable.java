package com.unvus.query;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class NvSqlTable extends SqlTable {
    protected NvSqlTable(String tableName) {
        super(tableName);
    }

    protected NvSqlTable(Supplier<String> tableNameSupplier) {
        super(tableNameSupplier);
    }

    protected NvSqlTable(Supplier<Optional<String>> schemaSupplier, String tableName) {
        super(schemaSupplier, tableName);
    }

    protected NvSqlTable(Supplier<Optional<String>> catalogSupplier, Supplier<Optional<String>> schemaSupplier, String tableName) {
        super(catalogSupplier, schemaSupplier, tableName);
    }

    public String getAlias() {
        return null;
    }

    public abstract Map<String, SqlColumn> getFieldMap();

}
