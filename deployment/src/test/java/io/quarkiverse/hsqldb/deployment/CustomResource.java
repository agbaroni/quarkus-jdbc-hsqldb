package io.quarkiverse.hsqldb.deployment;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import io.agroal.api.AgroalDataSource;
import io.smallrye.common.annotation.Blocking;

@Blocking
@Path("/customResource")
@Transactional
public class CustomResource {

    @Inject
    AgroalDataSource dataSource;

    @PostConstruct
    public void setup() throws Exception {
        try (var connection = dataSource.getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE DATA (KEY VARCHAR(32) PRIMARY KEY, VALUE VARCHAR(64))");
            }

            try (var statement = connection
                    .prepareStatement("INSERT INTO DATA (KEY, VALUE) VALUES ('hello', 'world')")) {
                statement.execute();
            }
        }
    }

    @GET
    @Path("/{key}")
    public String getValue(@PathParam("key") String key) throws Exception {
        try (var connection = dataSource.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT VALUE FROM DATA WHERE KEY = ?")) {
                statement.setString(1, key);

                try (var resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new NotFoundException();
                    }

                    return resultSet.getString(1);
                }
            }
        }
    }
}
