package com.unvus.query.parser;

import com.unvus.query.QueryBuilder;

@FunctionalInterface
public interface ParserPlugin {
    void parse(QueryBuilder qb);
}
