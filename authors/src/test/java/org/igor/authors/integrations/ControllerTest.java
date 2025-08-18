package org.igor.authors.integrations;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ControllerTest {

    @RegisterExtension
    static WireMockExtension vm = WireMockExtension.newInstance()
            .options(
                    wireMockConfig()
                            .port(46666)
            )
            .build();

    @Test
    public void searchTest(EmbeddedServer server) {
       vm.stubFor(
                get("/authors/search")
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("json-search-test.json")
                                        .withStatus(200)
                        )
        );

        List<Map<String, String>> result = given().
                baseUri("http://localhost").
                port(server.getPort()).
                when().
                get("/authors/search").as(new TypeRef<List<Map<String, String>>>() {});

       assertThat(result, hasSize(2));
    }

}
