package io.quarkiverse.hsqldb.deployment;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Optional;

import org.hibernate.dialect.HSQLDialect;
import org.jboss.logging.Logger;

import io.quarkiverse.hsqldb.runtime.HyperSQLAgroalConnectionConfigurer;
import io.quarkiverse.hsqldb.runtime.HyperSQLConstants;
import io.quarkus.agroal.spi.JdbcDriverBuildItem;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.datasource.deployment.spi.DefaultDataSourceDbKindBuildItem;
import io.quarkus.datasource.deployment.spi.DevServicesDatasourceConfigurationHandlerBuildItem;
import io.quarkus.datasource.deployment.spi.DevServicesDatasourceContainerConfig;
import io.quarkus.datasource.deployment.spi.DevServicesDatasourceProvider;
import io.quarkus.datasource.deployment.spi.DevServicesDatasourceProviderBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.hibernate.orm.deployment.spi.DatabaseKindDialectBuildItem;
import io.quarkus.runtime.LaunchMode;

public class HyperSQLProcessor {

    private static final String FEATURE = "jdbc-hsqldb";
    private static final Logger LOGGER = Logger.getLogger(HyperSQLProcessor.class);

    @BuildStep
    public AdditionalBeanBuildItem configureAgroalConnection(Capabilities capabilities) {
        if (capabilities.isPresent(Capability.AGROAL)) {
            return new AdditionalBeanBuildItem //
                    .Builder() //
                    .addBeanClass(HyperSQLAgroalConnectionConfigurer.class) //
                    .setUnremovable().build();
        }

        return null;
    }

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public DatabaseKindDialectBuildItem databaseKindDialect() {
        return DatabaseKindDialectBuildItem.forThirdPartyDialect(HyperSQLConstants.DB_KIND,
                HSQLDialect.class.getName());
    }

    @BuildStep
    public DefaultDataSourceDbKindBuildItem defaultDataSourceDbKind() {
        return new DefaultDataSourceDbKindBuildItem(HyperSQLConstants.DB_KIND);
    }

    @BuildStep
    public DevServicesDatasourceConfigurationHandlerBuildItem devServicesDatasourceConfigurationHandler() {
        return DevServicesDatasourceConfigurationHandlerBuildItem.jdbc(HyperSQLConstants.DB_KIND);
    }

    @BuildStep
    public DevServicesDatasourceProviderBuildItem devServicesDatasourceProvider() {
        return new DevServicesDatasourceProviderBuildItem(HyperSQLConstants.DB_KIND,
                new DevServicesDatasourceProvider() {
                    private static final String PASSWORD = "";
                    private static final String URL = "jdbc:hsqldb:mem:development";
                    private static final String USERNAME = "SA";

                    private Connection connection = null;

                    @Override
                    public boolean isDockerRequired() {
                        return false;
                    }

                    @Override
                    public RunningDevServicesDatasource startDatabase(Optional<String> username,
                            Optional<String> password, String datasourceName,
                            DevServicesDatasourceContainerConfig devServicesDatasourceContainerConfig,
                            LaunchMode launchMode, Optional<Duration> startupTimeout) {
                        try {
                            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                            LOGGER.info("Dev Services for HyperSQL started.");

                            return new RunningDevServicesDatasource(null, URL,
                                    null, USERNAME, PASSWORD, new Closeable() {
                                        @Override
                                        public void close() throws IOException {
                                            if (connection != null) {
                                                try {
                                                    connection.close();
                                                } catch (SQLException e) {
                                                    throw new IOException(e);
                                                }
                                            }

                                            LOGGER.info("Dev Services for HyperSQL shut down.");
                                        }
                                    });
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    @BuildStep
    public JdbcDriverBuildItem jdbcDriver() {
        return new JdbcDriverBuildItem(HyperSQLConstants.DB_KIND, HyperSQLConstants.DRIVER_NAME,
                HyperSQLConstants.DATA_SOURCE_NAME);
    }

    @BuildStep
    public ReflectiveClassBuildItem reflectiveClass() {
        return ReflectiveClassBuildItem.builder(HyperSQLConstants.DRIVER_NAME).build();
    }
}
