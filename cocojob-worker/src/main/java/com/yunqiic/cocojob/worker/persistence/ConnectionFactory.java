package com.yunqiic.cocojob.worker.persistence;

import com.yunqiic.cocojob.common.utils.CommonUtils;
import com.yunqiic.cocojob.worker.common.constants.StoreStrategy;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.h2.Driver;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接管理
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Slf4j
public class ConnectionFactory {

    private volatile DataSource dataSource;

    private final String H2_PATH = System.getProperty("user.home") + "/cocojob/worker/h2/" + CommonUtils.genUUID() + "/";
    private final String DISK_JDBC_URL = String.format("jdbc:h2:file:%scocojob_worker_db;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false", H2_PATH);
    private final String MEMORY_JDBC_URL = String.format("jdbc:h2:mem:%scocojob_worker_db;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false", H2_PATH);

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public synchronized void initDatasource(StoreStrategy strategy) {
        // 兼容单元测试，否则没办法单独测试 DAO 层了
        strategy = strategy == null ? StoreStrategy.DISK : strategy;

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(Driver.class.getName());
        config.setJdbcUrl(strategy == StoreStrategy.DISK ? DISK_JDBC_URL : MEMORY_JDBC_URL);
        config.setAutoCommit(true);
        // 池中最小空闲连接数量
        config.setMinimumIdle(2);
        // 池中最大连接数量
        config.setMaximumPoolSize(32);
        dataSource = new HikariDataSource(config);

        log.info("[PowerDatasource] init h2 datasource successfully, use url: {}", config.getJdbcUrl());

        // JVM 关闭时删除数据库文件
        try {
            FileUtils.forceDeleteOnExit(new File(H2_PATH));
        }catch (Exception ignore) {
        }
    }

}
