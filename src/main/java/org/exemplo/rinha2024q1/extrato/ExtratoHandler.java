package org.exemplo.rinha2024q1.extrato;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static io.vertx.core.json.Json.encode;

public class ExtratoHandler {

    private static final String APPLICATION_JSON = "application/json";

    private final ExtratoRepository repository;

    public ExtratoHandler(ExtratoRepository repository) {
        this.repository = repository;
    }

    public Future<Extrato> consultar(RoutingContext context) {
        var cliente = Long.parseLong(context.pathParam("id"));
        return repository.consultar(cliente)
                .onSuccess(e -> context.response().putHeader(CONTENT_TYPE, APPLICATION_JSON).end(encode(e)))
                .onFailure(ex -> context.response().setStatusCode(404).end());
    }
}
