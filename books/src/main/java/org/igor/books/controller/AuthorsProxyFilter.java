package org.igor.books.controller;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.client.ProxyHttpClient;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;

@Filter("/authors/**")
public class AuthorsProxyFilter implements HttpServerFilter { // (1)

    private final ProxyHttpClient client;
    private final String targetSchema;
    private final String targetHost;
    private final int targetPort;

    public AuthorsProxyFilter(ProxyHttpClient client,
                              @Property(name="authors.target.schema") String targetProtocol,
                              @Property(name="authors.target.host") String targetHost,
                              @Property(name="authors.target.port") int targetPort) { // (2)
        this.client = client;
        this.targetSchema = targetProtocol;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request,
                                                      ServerFilterChain chain) {
        return Publishers.map(
                client.proxy( // (3)
                    request
                            .mutate() // (4)
                            .uri(b -> b
                                    .scheme(targetSchema)
                                    .host(targetHost)
                                    .port(targetPort)
                            )
                            .header("X-AUTHORS-Request-Header", "XXX") // (6)
                ),
                response -> response.header("X-AUTHORS-Response-Header", "YYY")
        );
    }
}