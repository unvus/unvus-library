package com.unvus.config.mybatis;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public abstract class UnvusMyBatisConfig {

	protected void configureSqlSessionFactory(SqlSessionFactoryBean sessionFactoryBean, DataSource dataSource, String typeAliasesPackage, String typeHandlerPackage, String mapperLocationsPath) throws IOException {
		UnvusMybatisConfiguration configuration = new UnvusMybatisConfiguration();
		configuration.setMapUnderscoreToCamelCase(true);
		configuration.setJdbcTypeForNull(JdbcType.NULL);

		PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
		sessionFactoryBean.setTypeHandlersPackage(typeHandlerPackage);
		sessionFactoryBean.setMapperLocations(pathResolver.getResources(mapperLocationsPath));

		sessionFactoryBean.setPlugins(getInterceptors());
		sessionFactoryBean.setConfiguration(configuration);
		sessionFactoryBean.setVfs(SpringBootVFS.class);
	}

	protected Interceptor[] getInterceptors() {
		return null;
	}
}
