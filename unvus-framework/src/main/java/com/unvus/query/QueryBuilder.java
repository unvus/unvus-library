package com.unvus.query;

import com.unvus.pagination.Pagination;
import com.unvus.pagination.SortBy;
import com.unvus.pagination.SortModel;
import com.unvus.query.frag.AndOr;
import com.unvus.query.frag.Oper;
import com.unvus.util.DateTools;
import com.unvus.util.FieldMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.*;
import org.mybatis.dynamic.sql.render.ExplicitTableAliasCalculator;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.where.AbstractWhereDSL;
import org.mybatis.dynamic.sql.where.WhereApplier;
import org.mybatis.dynamic.sql.where.WhereDSL;
import org.mybatis.dynamic.sql.where.render.WhereClauseProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.JDBCType;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.unvus.query.frag.Oper.EQ;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Slf4j
@Component
public class QueryBuilder {

    private static String dateFormat;

    private static String dateTimeFormat;

    @Inject
    public void setDateFormat(@Value("${unvus.format.date-format:yyyy.MM.dd}") String dateFormat) {
        this.dateFormat = dateFormat; // NOSONAR
    }

    @Inject
    public void setDateTimeFormat(@Value("${unvus.format.datetime-format:yyyy.MM.dd HH:mm:ss}") String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat; // NOSONAR
    }

    private FieldMap queryParam = new FieldMap();

    private List<NvSqlTable> sqlTableList;
    private Map<String, NvSqlTable> tableAliasMap = new HashMap<>();
    private Map<SqlTable, String> tableAliasMapReverse = new HashMap<>();
    private WhereDSL whereDsl;
    private Map<String, Object> requestParam;

//    public QueryBuilder(WhereClauseProvider whereClause) {
//        if(StringUtils.isNotEmpty(whereClause.getWhereClause())) {
//            queryParam.put("whereClause", StringUtils.removeStart(whereClause.getWhereClause(), "where "));
//            queryParam.put("parameters", whereClause.getParameters());
//        }
//    }

    private QueryBuilder() {
    }

    public static QueryBuilder getInstance() {
        return new QueryBuilder();
    }

    public static QueryBuilder of(WhereDSL whereDsl) {
        QueryBuilder qb = new QueryBuilder();
        return qb.whereDsl(whereDsl);
    }

    public static QueryBuilder of(Map<String, Object> requestParam, NvSqlTable sqlTableArr) {
        QueryBuilder qb = new QueryBuilder();
        return qb.init(requestParam, null, Arrays.asList(sqlTableArr));
    }

    public static QueryBuilder of(Map<String, Object> requestParam, NvSqlTable... sqlTableArr) {
        QueryBuilder qb = new QueryBuilder();
        return qb.init(requestParam, null, Arrays.asList(sqlTableArr));
    }

    public static QueryBuilder of(Map<String, Object> requestParam, List<NvSqlTable> sqlTableList) {
        QueryBuilder qb = new QueryBuilder();
        return qb.init(requestParam, null, sqlTableList);
    }

    public static QueryBuilder of(Map<String, Object> requestParam, WhereDSLHelper dslHelper, NvSqlTable sqlTableArr) {
        QueryBuilder qb = new QueryBuilder();
        return qb.init(requestParam, dslHelper, Arrays.asList(sqlTableArr));
    }

    public static QueryBuilder of(Map<String, Object> requestParam, WhereDSLHelper dslHelper, NvSqlTable... sqlTableArr) {
        QueryBuilder qb = new QueryBuilder();
        return qb.init(requestParam, dslHelper, Arrays.asList(sqlTableArr));
    }

    public static QueryBuilder of(Map<String, Object> requestParam, WhereDSLHelper dslHelper, List<NvSqlTable> sqlTableList) {
        QueryBuilder qb = new QueryBuilder();
        return qb.init(requestParam, dslHelper, sqlTableList);
    }

    private QueryBuilder init(Map<String, Object> requestParam, WhereDSLHelper dslHelper, List<NvSqlTable> sqlTableList) {
        this.requestParam = requestParam;
        this.sqlTableList = sqlTableList;


        if(CollectionUtils.isNotEmpty(sqlTableList)) {
            for (NvSqlTable sqlTable : this.sqlTableList) {
                this.tableAliasMap.put(sqlTable.getAlias(), sqlTable);
                this.tableAliasMapReverse.put(sqlTable, sqlTable.getAlias());
            }
        }

        if(dslHelper != null) {
            this.whereDsl = (WhereDSL) dslHelper.where(where());
        }

        return this;
    }

    public QueryBuilder whereDsl(WhereDSL whereDsl) {
        this.whereDsl = whereDsl;
        return this;
    }

    public WhereDSL whereDsl() {
        if(this.whereDsl == null) {
            this.whereDsl = where();
        }
        return this.whereDsl;
    }

    public QueryBuilder param(String key, Object val) {
        queryParam.put(key, val);
        return this;
    }

    public FieldMap build() {
        if(this.whereDsl == null) {
            this.whereDsl = where();
        }

        parseParam(null);

        WhereClauseProvider whereClause = whereDsl
            .build()
            .render(RenderingStrategies.MYBATIS3, ExplicitTableAliasCalculator.of(tableAliasMapReverse));

        if(StringUtils.isNotEmpty(whereClause.getWhereClause())) {
            queryParam.put("whereClause", StringUtils.removeStart(whereClause.getWhereClause(), "where "));
            queryParam.put("parameters", whereClause.getParameters());
        }

        // prevent sql injection
        SortModel psm = Pagination.sortModel.get();
        if(psm != null && psm.getSortByList() != null) {
            List<SortBy> newSortByList = new ArrayList();
            for (SortBy sortBy : psm.getSortByList()) {
                if (sortBy.isChecked()) {
                    newSortByList.add(sortBy);
                    continue;
                }
                try {
                    String[] keyArr = StringUtils.split(sortBy.getSortKey(), '.');
                    NvSqlTable table = tableAliasMap.get(keyArr[0]);
                    SqlColumn sqlColumn = table.getFieldMap().get(keyArr[1]);
                    sortBy.setSortKey(sqlColumn.renderWithTableAlias(ExplicitTableAliasCalculator.of(table, table.getAlias())));
                    newSortByList.add(sortBy);
                } catch (Exception ignore) {}
            }
            psm.setSortByList(newSortByList);
        }

        return queryParam;
    }

    public WhereApplier applier(WhereDSLHelper helper) {
        WhereApplier applier = (d) -> {
            helper.where(d);
            parseParam(d);
        };
        return applier;
    }

    @FunctionalInterface
    public interface WhereDSLHelper {
        AbstractWhereDSL where(AbstractWhereDSL dsl);
    }

    private void parseParam(AbstractWhereDSL whereDsl) {

        if(whereDsl == null) {
            whereDsl = this.whereDsl;
        }
        if(this.requestParam == null || this.requestParam.isEmpty()) {
            return;
        }
        parse(whereDsl, requestParam);
    }

    private void parse(AbstractWhereDSL whereDsl, Map<String, Object> param) {
        for(Map.Entry<String, Object> entry: param.entrySet()) {
            String key = entry.getKey();
            boolean addedToDsl = false;
            if("_d".equals(key)) {
                // dynamic
                for(Map.Entry<String, Object> entry1: ((Map<String, Object>)entry.getValue()).entrySet()) {
                    Map<String, Object> dItem = (Map)entry1.getValue();
                    if(StringUtils.isBlank((String)dItem.get("val"))) {
                        continue;
                    }
                    Map dType = (Map)dItem.get("type");
                    if(dType.get("value") instanceof String) {
                        // 구 로직 (호환용)
                        String dKey = (String)dType.get("value");

                        String[] flds = StringUtils.split(dKey, ',');
                        if(flds == null || flds.length == 0) {
                            continue;
                        }
                        String[] keyArr = StringUtils.split(flds[0], '.');
                        try {
                            NvSqlTable sqlTable = tableAliasMap.get(keyArr[0]);
                            SqlColumn sqlColumn = sqlTable.getFieldMap().get(keyArr[1]);
                            if(flds.length > 1) {
                                List<AndOrCriteriaGroup> criterionList = new ArrayList<>();
                                flds = ArrayUtils.remove(flds, 0);
                                for(String fld: flds) {
                                    String[] nestedKeyArr = StringUtils.split(fld, '.');
                                    SqlColumn nestedSqlColumn = tableAliasMap.get(nestedKeyArr[0]).getFieldMap().get(nestedKeyArr[1]);
                                    criterionList.add(or(nestedSqlColumn, retrieveCond(Oper.LIKE_FULL, dItem.get("val"))));
                                }
                                whereDsl.and(sqlColumn, retrieveCond(Oper.LIKE_FULL, dItem.get("val")), criterionList);
                            }else {
                                whereDsl.and(sqlColumn, retrieveCond(Oper.LIKE_FULL, dItem.get("val")));
                            }
                            addedToDsl = true;
                        }catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }else {
                        // 신 로직
                        List<Map<String, String>> flds = (List)dType.get("value");
                        if(flds == null || flds.size() == 0) {
                            continue;
                        }
                        String keyword = (String)dItem.get("val");

                        try {
                            List<Map<String, String>> filteredFields = new ArrayList<>();
                            // 타입이 맞지 않는 조건 제외
                            for(Map<String, String> fld: flds) {
                                String[] keyArr = StringUtils.split(fld.get("name"), '.');

                                SqlColumn sqlColumn = tableAliasMap.get(keyArr[0]).getFieldMap().get(keyArr[1]);
                                Oper oper = Oper.LIKE_FULL;
                                if(fld.containsKey("op")) {
                                    oper = Oper.getByCode(fld.get("op"));
                                }
                                if(isAcceptable(keyword, sqlColumn, oper)) {
                                    filteredFields.add(fld);
                                }
                            }
                            if(filteredFields.size() == 0) {
                                continue;
                            }

                            Map<String, String> firstField = filteredFields.get(0);
                            String[] keyArr = StringUtils.split(firstField.get("name"), '.');

                            NvSqlTable sqlTable = tableAliasMap.get(keyArr[0]);
                            SqlColumn sqlColumn = sqlTable.getFieldMap().get(keyArr[1]);
                            Oper oper = Oper.LIKE_FULL;
                            if(firstField.containsKey("op")) {
                                oper = Oper.getByCode(firstField.get("op"));
                            }

                            if(filteredFields.size() > 1) {
                                List<AndOrCriteriaGroup> criterionList = new ArrayList<>();
                                filteredFields.remove(0);
                                for(Map<String, String> fld: filteredFields) {
                                    String[] nestedKeyArr = StringUtils.split(fld.get("name"), '.');
                                    SqlColumn nestedSqlColumn = tableAliasMap.get(nestedKeyArr[0]).getFieldMap().get(nestedKeyArr[1]);
                                    Oper nestedOper = Oper.LIKE_FULL;
                                    if(fld.containsKey("op")) {
                                        nestedOper = Oper.getByCode(fld.get("op"));
                                    }
                                    criterionList.add(or(nestedSqlColumn, retrieveCond(nestedOper, dItem.get("val"))));
                                }
                                whereDsl.and(sqlColumn, retrieveCond(oper, dItem.get("val")), criterionList);
                            }else {
                                whereDsl.and(sqlColumn, retrieveCond(oper, dItem.get("val")));
                            }
                            addedToDsl = true;
                        }catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                }
            }
            if(key.indexOf('.') > -1) {
                String[] keyArr = StringUtils.split(key, '.');
                NvSqlTable sqlTable = tableAliasMap.get(keyArr[0]);

                if(sqlTable != null) {
                    Object entryValue = entry.getValue();
                    AndOr andOr = AndOr.AND;
                    Oper oper = EQ;
                    Object value;
                    if(entryValue instanceof Map) {
                        Map valMap = (Map)entryValue;
                        if(valMap.containsKey("from") || valMap.containsKey("to")) {
                            parsePeriodParam(entry, Oper.DATE_FROM, whereDsl);
                            parsePeriodParam(entry, Oper.DATE_TO, whereDsl);
                            continue;
                        }else {
                            String ao = (String)valMap.get("ao");
                            if(StringUtils.isNotBlank(ao)) {
                                andOr = AndOr.getByCode(ao);
                            }
                            String op = (String)valMap.get("op");
                            if(StringUtils.isNotBlank(op)) {
                                oper = Oper.getByCode(op);
                            }

                            value = valMap.get("val");
                        }
                    }else {
                        value = entryValue;
                    }

                    boolean validated = validateCond(value, oper);
                    if(validated) {
                        SqlColumn sqlColumn = sqlTable.getFieldMap().get(keyArr[1]);
                        if(sqlColumn != null) {
                            if (andOr == AndOr.AND) {
                                whereDsl.and(sqlColumn, retrieveCond(oper, value));
                            } else {
                                whereDsl.or(sqlColumn, retrieveCond(oper, value));
                            }
//                        this.cond(new Cond(sqlTable.getField(keyArr[1]), value, oper, andOr));
//                            addedToDsl = true;
                        }
                    }
                }
            }

            if(!addedToDsl) {
                if (key.indexOf('.') > -1) {
                    String[] keyArr = StringUtils.split(key, '.');
                    if(!(queryParam.get(keyArr[0]) instanceof Map)) {
                        queryParam.put(keyArr[0], new HashMap<>());
                    }
                    ((Map)queryParam.get(keyArr[0])).put(keyArr[1], entry.getValue());
                }else {
                    queryParam.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }


    private boolean validateCond(Object val, Oper oper) {
        boolean validated = true;

        // 값 검증
        if(val == null && !(oper == Oper.IS || oper == Oper.IS_NOT)) {
            validated = false;
        }else if(val instanceof String && StringUtils.isBlank((String)val)) {
            validated = false;
        }else if(val instanceof Collection && CollectionUtils.isEmpty((Collection)val)) {
            validated = false;
        }
        return validated;
    }

    private void parsePeriodParam(Map.Entry<String, Object> entry, Oper oper, AbstractWhereDSL whereDsl) {
        String fromTo = "from";
        if(oper == Oper.DATE_TO) {
            fromTo = "to";
        }
        String key = entry.getKey();
        String[] keyArr = StringUtils.split(key, '.');
        Map valMap = (Map)entry.getValue();
        NvSqlTable sqlTable = tableAliasMap.get(keyArr[0]);

        if(valMap.containsKey(fromTo)) {
            Map dateMap = (Map)valMap.get(fromTo);
            if(Boolean.TRUE.equals(dateMap.get("enabled"))) {
                String value = (String)dateMap.get("value");
                if(StringUtils.isNotBlank(value)) {
                    SqlColumn sqlColumn = sqlTable.getFieldMap().get(keyArr[1]);

                    if(value.length() == this.dateFormat.length()) {
                        LocalDate temp = DateTools.convertFromFormattedStr(value, dateFormat, DateTools.ConvertTo.LOCAL_DATE);
                        if(oper == Oper.DATE_TO) {
                            temp = temp.plusDays(1L);
                            whereDsl.and(sqlColumn, isLessThan(temp));
                        }else {
                            whereDsl.and(sqlColumn, retrieveCond(oper, temp));
                        }
                    }else if(value.length() == this.dateTimeFormat.length()) {
                        LocalDateTime temp = DateTools.convertFromFormattedStr(value, dateTimeFormat, DateTools.ConvertTo.LOCAL_DATE_TIME);
                        whereDsl.and(sqlColumn, retrieveCond(oper, temp));
                    }else {
                        whereDsl.and(sqlColumn, retrieveCond(oper, value));
                    }
                }
            }
        }
    }

    private VisitableCondition retrieveCond(Oper oper, Object value) {
        return oper.getFunc().apply(value);
    }

    enum TYPE_CATEGORY {
        NUMERIC, STRING, BOOLEAN, DATE, OTHER
    }
    private TYPE_CATEGORY getTypeCategory(JDBCType jdbcType) {
        if(jdbcType == JDBCType.BIT
            || jdbcType == JDBCType.TINYINT
            || jdbcType == JDBCType.SMALLINT
            || jdbcType == JDBCType.INTEGER
            || jdbcType == JDBCType.BIGINT
            || jdbcType == JDBCType.FLOAT
            || jdbcType == JDBCType.TINYINT
            || jdbcType == JDBCType.BIGINT
            || jdbcType == JDBCType.FLOAT
            || jdbcType == JDBCType.REAL
            || jdbcType == JDBCType.DOUBLE
            || jdbcType == JDBCType.NUMERIC
            || jdbcType == JDBCType.DECIMAL) {
            return TYPE_CATEGORY.NUMERIC;
        }else if(jdbcType == JDBCType.DATE
            || jdbcType == JDBCType.TIME
            || jdbcType == JDBCType.TIMESTAMP
            || jdbcType == JDBCType.TIME_WITH_TIMEZONE
            || jdbcType == JDBCType.TIMESTAMP_WITH_TIMEZONE) {
            return TYPE_CATEGORY.DATE;
        }else if(jdbcType == JDBCType.CHAR
            || jdbcType == JDBCType.VARCHAR
            || jdbcType == JDBCType.LONGVARCHAR
            || jdbcType == JDBCType.BINARY
            || jdbcType == JDBCType.VARBINARY
            || jdbcType == JDBCType.LONGVARBINARY
            || jdbcType == JDBCType.NULL
            || jdbcType == JDBCType.BLOB
            || jdbcType == JDBCType.CLOB
            || jdbcType == JDBCType.NVARCHAR
            || jdbcType == JDBCType.NCHAR
            || jdbcType == JDBCType.NCLOB
            || jdbcType == JDBCType.STRUCT
            || jdbcType == JDBCType.DISTINCT
            || jdbcType == JDBCType.LONGNVARCHAR
            || jdbcType == JDBCType.SQLXML) {
        }else if(jdbcType == JDBCType.BOOLEAN) {
            return TYPE_CATEGORY.BOOLEAN;
        }
        return TYPE_CATEGORY.OTHER;
    }

    private boolean isAcceptable(String keyword, SqlColumn sqlColumn, Oper oper) {
        boolean isAcceptable = true;
        if(sqlColumn.jdbcType().isPresent()) {
            JDBCType jdbcType = (JDBCType) sqlColumn.jdbcType().get();
            if(oper == EQ && getTypeCategory(jdbcType) == TYPE_CATEGORY.NUMERIC && !NumberUtils.isParsable(keyword)) {
                isAcceptable = false;
            }
        }
        return isAcceptable;
    }

}
