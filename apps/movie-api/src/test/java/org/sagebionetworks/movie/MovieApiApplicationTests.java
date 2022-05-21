package org.sagebionetworks.movie;

import com.auth0.jwk.JwkProvider;
import org.sagebionetworks.movie.infra.security.config.KeycloakJwkProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
@Testcontainers
public class MovieApiApplicationTests {

    @Container
    private static final GenericContainer keycloak = new GenericContainer(DockerImageName.parse("sagebionetworks/challenge-registry-keycloak:latest"))
            .withExposedPorts(8080)
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "changeme")
            .withEnv("DB_VENDOR", "h2")
            .withClasspathResourceMapping("keycloak/test-realm.json", "/opt/keycloak/data/import/test-realm.json", BindMode.READ_ONLY)
            .withClasspathResourceMapping("keycloak/test-users-0.json", "/opt/keycloak/data/import/test-users-0.json", BindMode.READ_ONLY)
            .withCommand("start-dev --import-realm")
            .waitingFor(Wait.forHttp("realms/master"));

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Try to get all movies (request without Authorization header)")
    void requestAllMoviesWithoutAuthorizationHeader() throws Exception {

        mockMvc.perform(
                get("/movies"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get all movies (request with Authorization header)")
    void getAllMoviesWithAuthorizationHeader() throws Exception {

        String accessToken = fetchAccessToken("ADMIN");

        mockMvc.perform(
                get("/movies")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get a single movie (request with Authorization header)")
    void getSingleMovieWithAuthorizationHeader() throws Exception {

        String accessToken = fetchAccessToken("VISITOR");

        mockMvc.perform(
                get("/movies/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Try to get a single movie having wrong role (request with Authorization header)")
    void getSingleHavingIncorrectUserRole() throws Exception {

        String accessToken = fetchAccessToken("VISITOR");

        mockMvc.perform(
                get("/movies")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private String fetchAccessToken(String role) {

        String username = role.equals("ADMIN") ? "han" : "luke";

        @SuppressWarnings("HttpUrlsUsage") String keycloakUrl = "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080) + "/realms/test";

        Map<String, String> formParams = Map.of(
                "grant_type", "password",
                "scope", "openid",
                "client_id", "test-client",
                "client_secret", "MiZwMWx3Tuw1mkySKId10lk7kPgKV9IZ",
                "username", username,
                "password", "changeme"
        );

        var response =
                given()
                    .contentType("application/x-www-form-urlencoded")
                    .accept("application/json, text/plain, */*")
                    .formParams(formParams)
                        .log().all()
                .when()
                    .post(keycloakUrl + "/protocol/openid-connect/token")
                        .prettyPeek()
                .then();

        response.statusCode(200);

        return response.extract().body().jsonPath().getString("access_token");
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {

        @Bean
        @Primary
        public JwkProvider keycloakJwkProvider() {
            @SuppressWarnings("HttpUrlsUsage")
            String jwkUrl = "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080) + "/realms/test" + "/protocol/openid-connect/certs";
            return new KeycloakJwkProvider(jwkUrl);
        }
    }
}
