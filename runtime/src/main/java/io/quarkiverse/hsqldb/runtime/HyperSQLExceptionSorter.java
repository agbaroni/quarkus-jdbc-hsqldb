package io.quarkiverse.hsqldb.runtime;

import java.sql.SQLException;

import io.agroal.api.configuration.AgroalConnectionPoolConfiguration.ExceptionSorter;

public class HyperSQLExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isFatal(SQLException e) {
        return false;
    }
}
