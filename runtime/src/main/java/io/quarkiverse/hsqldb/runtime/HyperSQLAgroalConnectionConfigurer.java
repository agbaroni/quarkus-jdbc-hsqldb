package io.quarkiverse.hsqldb.runtime;

import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.quarkus.agroal.runtime.AgroalConnectionConfigurer;
import io.quarkus.agroal.runtime.JdbcDriver;

@JdbcDriver(HyperSQLConstants.DB_KIND)
public class HyperSQLAgroalConnectionConfigurer implements AgroalConnectionConfigurer {

    @Override
    public void setExceptionSorter(String databaseKind, AgroalDataSourceConfigurationSupplier dataSourceConfiguration) {
        dataSourceConfiguration.connectionPoolConfiguration().exceptionSorter(new HyperSQLExceptionSorter());
    }
}
