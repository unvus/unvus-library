package com.unvus.config.mybatis.type;

import com.unvus.config.mybatis.customize.BaseTypeAwareTypeHandler;
import com.unvus.domain.EnumCode;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Created by guava on 16.8.17.
 */
@Alias("enumCodeTypeHandler")
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(EnumCode.class)
public class EnumCodeTypeHandler extends BaseTypeAwareTypeHandler<EnumCode> {

    EnumCode[] enumValArr;

    public EnumCodeTypeHandler(Class<EnumCode> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.enumValArr = type.getEnumConstants();
        if (this.enumValArr == null) {
//            throw new IllegalArgumentException(type.getSimpleName()
//                + " does not represent an enum type.");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EnumCode parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public EnumCode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        if (result != null) {
            return getByCode(result);
        }
        return null;
    }

    @Override
    public EnumCode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if (result != null) {
            return getByCode(result);
        }
        return null;
    }

    @Override
    public EnumCode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        if (result != null) {
            return getByCode(result);
        }
        return null;
    }

    public EnumCode getByCode(String code) {
        EnumCode result = null;
//        Enum[] enumValArr = ((Class<? extends Enum<?>>)javaType.getRawClass()).getEnumConstants();
        for(EnumCode enumCode : enumValArr) {
            if(StringUtils.equals(code, enumCode.getCode())) {
                result = enumCode;
                break;
            }
        }
        return result;
    }
}
