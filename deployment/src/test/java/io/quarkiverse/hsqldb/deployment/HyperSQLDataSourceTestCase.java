package io.quarkiverse.hsqldb.deployment;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.QuarkusUnitTest;

public class HyperSQLDataSourceTestCase {

    @RegisterExtension
    public static final QuarkusUnitTest test = new QuarkusUnitTest();

    @Inject
    AgroalDataSource dataSource;

    @Test
    public void testDataSource() throws Exception {
        if (dataSource == null) {
            fail("DataSource is null");
        } else {
            var configuration = dataSource.getConfiguration().connectionPoolConfiguration();

            assertTrue(configuration.connectionFactoryConfiguration().jdbcUrl().startsWith("jdbc:hsqldb"));

            try (var connection = dataSource.getConnection()) {
            }
        }
    }
}
