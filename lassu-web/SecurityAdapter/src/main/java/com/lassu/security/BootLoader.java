/**
 * 
 */
package com.lassu.security;

import static reactor.bus.selector.Selectors.$;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import com.lassu.security.common.util.CommonConstant;
import com.lassu.security.service.AuthChkReqAuditConsumer;

//import com.lassu.common.util.CommonConstant;
//import com.lassu.service.AuthDispatcher;

import reactor.Environment;
import reactor.bus.EventBus;
import reactor.core.config.DispatcherType;

/**
 * @author abhinab
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.lassu.security")
@PropertySource(value = { "classpath:datasource.properties" })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, JmsAutoConfiguration.class,
		SecurityAutoConfiguration.class })
public class BootLoader implements CommandLineRunner {

	private volatile static EventBus EVENT_BUS = null;
	private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
	public static int REACTOR_THREAD_COUNT;

	static {
		try {
			REACTOR_THREAD_COUNT = Integer
					.parseInt(System.getProperty("reactor.threads", String.valueOf(AVAILABLE_PROCESSORS)));
		} catch (Exception e) {
			REACTOR_THREAD_COUNT = AVAILABLE_PROCESSORS;
		}
	}

	public static EventBus getEventBus() {
		while (EVENT_BUS == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		return EVENT_BUS;
	}

	@Bean
	Environment env() {
		return Environment.initializeIfEmpty().assignErrorJournal();
	}

	@Bean
	EventBus createEventBus(Environment env) {
		EventBus evBus = EventBus.create(env, Environment.newDispatcher(REACTOR_THREAD_COUNT, REACTOR_THREAD_COUNT,
				DispatcherType.THREAD_POOL_EXECUTOR));
		EVENT_BUS = evBus;
		return EVENT_BUS;
	}

	@Autowired
	private EventBus eventBus;

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public DataSourceInitializer dataSourceInitializer() {
		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(dataSource());
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource("data.sql"));
		dataSourceInitializer.setDatabasePopulator(databasePopulator);
		dataSourceInitializer.setEnabled(Boolean.parseBoolean("true"));
		return dataSourceInitializer;
	}

	@Bean
	@Primary
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	@Primary
	public NamedParameterJdbcTemplate jdbcOltpNTemplate() {
		return new NamedParameterJdbcTemplate(dataSource());
	}

	@Bean
	@Primary
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Autowired
	private AuthChkReqAuditConsumer authChkReqAuditConsumer;
	
	@Override
	public void run(String... args) throws Exception {
		 eventBus.on($(CommonConstant.COMPONENT_AUTHCHK_REQ_AUDIT_DISPATCHER), authChkReqAuditConsumer);
	}

	public static void main(String[] args) {
		SpringApplication.run(BootLoader.class, args);
	}

	@PreDestroy
	void shutdownBus() {
		Environment.terminate();
	}

}
