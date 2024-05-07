package ar.edu.itba.paw.persistence;

import org.hsqldb.jdbc.JDBCDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@ComponentScan({ "ar.edu.itba.paw.persistence" })
@Configuration
@EnableTransactionManagement
public class TestConfig {
    
    @Value("classpath:schema.sql")
    private Resource schemaSql;

    @Value("classpath:hsqldb.sql")
    private Resource hsqldbSql;

    @Bean
    public DataSource dataSource() {
        final SingleConnectionDataSource ds = new SingleConnectionDataSource();
        ds.setDriverClassName(JDBCDriver.class.getName());
        ds.setSuppressClose(true);
        ds.setUrl("jdbc:hsqldb:mem:paw2");
        ds.setUsername("ha");
        ds.setPassword("");

        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan("ar.edu.itba.paw.models");
        emf.setDataSource(dataSource());

        final JpaVendorAdapter jva = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(jva);

        final Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "update");

        // development only, don't use this in production!
    /*    props.setProperty("format_sql", "true");
        props.setProperty("hibernate.show_sql", "true");
        */

        emf.setJpaProperties(props);

        return emf;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        final DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(dataSource());
        dsi.setDatabasePopulator(databasePopulator());

        return dsi;
    }
    
    private DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(hsqldbSql);
        populator.addScript(schemaSql);
        return populator;
    }


}
