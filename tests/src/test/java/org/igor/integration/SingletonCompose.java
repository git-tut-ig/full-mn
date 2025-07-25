package org.igor.integration;

import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

public abstract class SingletonCompose {

    private static final String AUTHORS_BASE_URL = System.getenv("AUTHORS_BASE_URL");
    private static final String WIREMOCK_HOST = System.getenv("WIREMOCK_HOST");
    private static final int WIREMOCK_PORT =Integer.parseInt(System.getenv("WIREMOCK_PORT"));

    private final static boolean ENABLED = true;    // For debug purposes

    private static boolean isStarted = false;
    private static ComposeContainer container;

    public static synchronized void start() {
        if (! isStarted) {
            if (ENABLED) {
                container = new ComposeContainer(new File("src/test/resources/docker-compose.yml"))
                        .withExposedService("wiremock", 8080, Wait.forListeningPort())
                        .withExposedService("books", 8080, Wait.forListeningPort())
                        .withExposedService("authors", 8080, Wait.forListeningPort());

                container.start();
            }

            isStarted = true;
        }
    }

    public static ComposeContainer getContainer() {
        start();
        return container;
    }

}
