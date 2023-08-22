package com.unvus.config.mybatis.type;

import com.fasterxml.jackson.core.type.TypeReference;
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

@Alias("varcharJsonTypeHandler")
@MappedJdbcTypes(JdbcType.VARCHAR)
public class VarcharJsonTypeHandler extends BaseTypeHandler<JsonMap> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonMap parameter, JdbcType jdbcType) throws SQLException {
        final String parameterString = mapToJson(parameter);
        ps.setString(i, parameterString);
    }

    public JsonMap getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        if (result != null) {
            return jsonToMap(result);
        }
        return null;
    }

    public JsonMap getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if (result != null) {
            return jsonToMap(result);
        }
        return null;
    }

    public JsonMap getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        if (result != null) {
            return jsonToMap(result);
        }
        return null;
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

