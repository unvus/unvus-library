package com.unvus.config.properties;

import com.zaxxer.hikari.HikariConfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseDatabaseProperties implements DatabaseProperties {

	private final HikariConfig hikari = new HikariConfig();

	private String driverClassName;

	private String url;

	private String username;

	private String password;

	private String name;

	private boolean cachePrepStmts = true;
	private boolean autoCommit = true;

	private int prepStmtCacheSize = 250;

	private int prepStmtCacheSqlLimit = 2048;

	private boolean useServerPrepStmts = true;


	private String host;

	private int port = 1521;
}
