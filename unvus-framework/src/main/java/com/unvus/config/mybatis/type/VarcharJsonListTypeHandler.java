package com.unvus.config.mybatis.type;

import com.unvus.util.JsonUtil;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Alias("varcharJsonTypeHandler")
@MappedJdbcTypes(JdbcType.VARCHAR)
public class VarcharJsonListTypeHandler extends BaseTypeHandler<List> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
        final String parameterString = mapToJson(parameter);
        ps.setString(i, parameterString);
    }

    public List getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        if (result != null) {
            return jsonToMap(result);
        }
        return null;
    }

    public List getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if (result != null) {
            return jsonToMap(result);
        }
        return null;
    }

    public List getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        if (result != null) {
            return jsonToMap(result);
        }
        return null;
    }

    private List jsonToMap(String from) {
        try {
            return JsonUtil.toListObject(from, String.class);
        } catch (IOException e) {
            throw new Error();
        }
    }

    private String mapToJson(List<String> from) {
        try {
            return JsonUtil.toJson(from);
        } catch (IOException e) {
            throw new Error();
        }
    }
}

