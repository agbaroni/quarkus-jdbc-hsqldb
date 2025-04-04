package io.quarkiverse.hsqldb.deployment;

import org.hamcrest.Matchers;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;

public class HyperSQLDataSourceDevModeTestCase {

    @RegisterExtension
    public static final QuarkusDevModeTest test = new QuarkusDevModeTest() //
            .withApplicationRoot(archive -> {
                archive.addClass(CustomResource.class)
                        .addAsResource(new StringAsset(""), "application.properties");
            });

    @Test
    public void testDataSource() throws Exception {
        RestAssured.get("/customResource/hello").then().statusCode(200).body(Matchers.equalTo("world"));
    }
}
