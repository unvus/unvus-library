package com.unvus.config.mybatis.type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unvus.util.FieldMap;
import com.unvus.util.JsonMap;
import com.unvus.util.JsonUtil;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.util.Map;

@Alias("jsonTypeHandler")
@MappedJdbcTypes(JdbcType.CLOB)
public class ClobJsonTypeHandler extends BaseTypeHandler<JsonMap> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonMap parameter, JdbcType jdbcType) throws SQLException {
        final String parameterString = mapToJson(parameter);
        StringReader reader = new StringReader(parameterString);
        ps.setCharacterStream(i, reader, parameterString.length());
    }

    public JsonMap getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = "";
        Clob clob = rs.getClob(columnName);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1L, size);
        }

        return jsonToMap(value);
    }

    public JsonMap getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = "";
        Clob clob = rs.getClob(columnIndex);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1L, size);
        }

        return jsonToMap(value);
    }

    public JsonMap getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = "";
        Clob clob = cs.getClob(columnIndex);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1L, size);
        }

        return jsonToMap(value);
    }

    private JsonMap jsonToMap(String from) {
        try {
            return JsonUtil.getMapper().readValue(from, new TypeReference<JsonMap>() {});
        } catch (IOException e) {
            throw new Error();
        }
    }

    private String mapToJson(Map<String, Object> from) {
        try {
            return JsonUtil.toJson(from);
        } catch (IOException e) {
            throw new Error();
        }
    }
}

