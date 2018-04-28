package com.vin.core.druid;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * druid连接池配置
 * @author 
 *
 */
@Configuration
@MapperScan(basePackages = MasterDruidDSConfig.PACKAGE, sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDruidDSConfig {
	private Logger logger = LoggerFactory.getLogger(MasterDruidDSConfig.class);

	// 精确到 master 目录，以便跟其他数据源隔离
	static final String PACKAGE = "com.vin.dao";
	// mybatis-config.xml 配置
	@Value("${mybatis.config-location}")
	private String MYBATIS_CONFIG;
	static final String MAPPER_LOCATION = "classpath:mybatis/master/*/*.xml";

	@Value("${master.datasource.url}")
	private String url;

	@Value("${master.datasource.username}")
	private String user;

	@Value("${master.datasource.password}")
	private String password;

	@Value("${master.datasource.driverClassName}")
	private String driverClass;

	/* 方式一
	 * @Value("${spring.datasource.initialSize}")
	private int initialSize;

	@Value("${spring.datasource.minIdle}")
	private int minIdle;

	@Value("${spring.datasource.maxActive}")
	private int maxActive;

	@Value("${spring.datasource.maxWait}")
	private int maxWait;

	@Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
	private int timeBetweenEvictionRunsMillis;

	@Value("${spring.datasource.minEvictableIdleTimeMillis}")
	private int minEvictableIdleTimeMillis;

	@Value("${spring.datasource.validationQuery}")
	private String validationQuery;

	@Value("${spring.datasource.testWhileIdle}")
	private boolean testWhileIdle;

	@Value("${spring.datasource.testOnBorrow}")
	private boolean testOnBorrow;

	@Value("${spring.datasource.testOnReturn}")
	private boolean testOnReturn;

	@Value("${spring.datasource.poolPreparedStatements}")
	private boolean poolPreparedStatements;

	@Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
	private int maxPoolPreparedStatementPerConnectionSize;

	@Value("${spring.datasource.filters}")
	private String filters;

	@Value("{spring.datasource.connectionProperties}")
	private String connectionProperties;*/
	
	@Autowired
	DruidProperties druiProperties;

	@Bean(name = "masterDataSource")
	@Primary
	public DataSource masterDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);

		// configuration
		/*方式一
		 * dataSource.setInitialSize(initialSize);
		dataSource.setMinIdle(minIdle);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWait);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setPoolPreparedStatements(poolPreparedStatements);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		try {
			dataSource.setFilters(filters);
		} catch (SQLException e) {
			logger.error("druid configuration initialization filter", e);
		}
		dataSource.setConnectionProperties(connectionProperties);*/
		
		// 方式二
		dataSource.setInitialSize(druiProperties.getInitialSize());
		dataSource.setMinIdle(druiProperties.getMinIdle());
		dataSource.setMaxActive(druiProperties.getMaxActive());
		dataSource.setMaxWait(druiProperties.getMaxWait());
		dataSource.setTimeBetweenEvictionRunsMillis(druiProperties.getTimeBetweenEvictionRunsMillis());
		dataSource.setMinEvictableIdleTimeMillis(druiProperties.getMinEvictableIdleTimeMillis());
		dataSource.setValidationQuery(druiProperties.getValidationQuery());
		dataSource.setTestWhileIdle(druiProperties.isTestWhileIdle());
		dataSource.setTestOnBorrow(druiProperties.isTestOnBorrow());
		dataSource.setTestOnReturn(druiProperties.isTestOnReturn());
		dataSource.setPoolPreparedStatements(druiProperties.isPoolPreparedStatements());
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(druiProperties.getMaxPoolPreparedStatementPerConnectionSize());
		try {
			dataSource.setFilters(druiProperties.getFilters());
		} catch (SQLException e) {
			logger.error("druid configuration initialization filter", e);
		}
		dataSource.setConnectionProperties(druiProperties.getConnectionProperties());
		
		return dataSource;
	}

	@Bean(name = "masterTransactionManager")
	@Primary
	public DataSourceTransactionManager masterTransactionManager() {
		return new DataSourceTransactionManager(masterDataSource());
	}

	@Bean(name = "masterSqlSessionFactory")
	@Primary
	public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource)
			throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(masterDataSource);
		sessionFactory.setConfigLocation(new ClassPathResource(MYBATIS_CONFIG));
		sessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(MasterDruidDSConfig.MAPPER_LOCATION));
		return sessionFactory.getObject();
	}
}
