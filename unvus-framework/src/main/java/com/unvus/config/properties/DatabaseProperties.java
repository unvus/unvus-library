package com.unvus.config.properties;

import com.zaxxer.hikari.HikariConfig;

public interface DatabaseProperties {
	String getDriverClassName();

	String getUrl();

	String getUsername();

	String getPassword();

	String getName();

	String getHost();

	int getPort();

	boolean isCachePrepStmts();
	boolean isAutoCommit();

	int getPrepStmtCacheSize();

	int getPrepStmtCacheSqlLimit();

	boolean isUseServerPrepStmts();

	HikariConfig getHikari();
}
