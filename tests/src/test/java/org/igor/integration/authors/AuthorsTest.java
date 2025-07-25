package org.igor.integration.authors;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.common.mapper.TypeRef;
import org.igor.integration.ResourceUtils;
import org.igor.integration.SingletonCompose;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthorsTest {

    private static final String AUTHORS_BASE_URL = System.getenv("AUTHORS_BASE_URL");
    private static final String WIREMOCK_HOST = System.getenv("WIREMOCK_HOST");
    private static final int WIREMOCK_PORT =Integer.parseInt(System.getenv("WIREMOCK_PORT"));

    @BeforeAll
    static void beforeAll() {
        SingletonCompose.start();
        WireMock.configureFor(WIREMOCK_HOST, WIREMOCK_PORT);
    }

    @AfterEach
    void afterEach() {
        WireMock.reset();
    }

    @Test
    public void searchTest() throws IOException {
        String response =  ResourceUtils.readJsonFileAsString("wire-mock/authors-search-response.json");

        stubFor(
                get("/authors/search")
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(response)
                                        .withStatus(200)
                        )
        );

        List<Map<String, String>> result = given().
                baseUri(AUTHORS_BASE_URL).
                when().
                get("/authors/search").as(new TypeRef<List<Map<String, String>>>() {});

        assertThat(result, hasSize(2));
    }

    @Test
    public void searchByFirstName() throws IOException {
        String response =  ResourceUtils.readJsonFileAsString("wire-mock/authors-search-parameters-response.json");

        StubMapping mapping = stubFor(
                get(urlMatching("/authors/search.*"))
                        .withQueryParam("firstName", equalTo("Anton"))
                        .withQueryParam("country", equalTo("Russia"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withHeader("X-WIREMOCK-Response", "XXX")
                                        .withBody(response)
                                        .withTransformers("response-template")
                                        .withStatus(200)
                        )
        );

        List<Map<String, String>> result =
                given().
                    baseUri(AUTHORS_BASE_URL).
                when().
                    get("/authors/search?firstName=Anton&country=Russia").as(new TypeRef<List<Map<String, String>>>() {});

        assertThat(result, hasSize(3));

        verify(exactly(1), getRequestedFor(urlPathMatching("/authors/.*"))
                .withQueryParam("firstName", containing("Anton"))
                .withQueryParam("country", equalTo("Russia")));
    }
}
