package com.yunqiic.cocojob.server.persistence.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * 本地H2数据库配置
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        // repository包名
        basePackages = LocalJpaConfig.LOCAL_PACKAGES,
        // 实体管理bean名称
        entityManagerFactoryRef = "localEntityManagerFactory",
        // 事务管理bean名称
        transactionManagerRef = "localTransactionManager"
)
public class LocalJpaConfig {

    @Resource(name = "omsLocalDatasource")
    private DataSource omsLocalDatasource;

    public static final String LOCAL_PACKAGES = "com.yunqiic.cocojob.server.persistence.local";

    private static Map<String, Object> genDatasourceProperties() {

        JpaProperties jpaProperties = new JpaProperties();
        jpaProperties.setOpenInView(false);
        jpaProperties.setShowSql(false);

        HibernateProperties hibernateProperties = new HibernateProperties();
        // 每次启动都删除数据（重启后原来的Instance已经通过故障转移更换了Server，老的日志数据也没什么意义了）
        hibernateProperties.setDdlAuto("create");
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

    @Bean(name = "localEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean initLocalEntityManagerFactory(EntityManagerFactoryBuilder builder) {

        return builder
                .dataSource(omsLocalDatasource)
                .properties(genDatasourceProperties())
                .packages(LOCAL_PACKAGES)
                .persistenceUnit("localPersistenceUnit")
                .build();
    }


    @Bean(name = "localTransactionManager")
    public PlatformTransactionManager initLocalTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(initLocalEntityManagerFactory(builder).getObject()));
    }

    @Bean(name = "localTransactionTemplate")
    public TransactionTemplate initTransactionTemplate(@Qualifier("localTransactionManager") PlatformTransactionManager ptm) {
        TransactionTemplate tt =  new TransactionTemplate(ptm);
        // 设置隔离级别
        tt.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        return tt;
    }
}
