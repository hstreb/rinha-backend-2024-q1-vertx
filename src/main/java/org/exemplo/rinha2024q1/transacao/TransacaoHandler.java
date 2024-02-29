package org.exemplo.rinha2024q1.transacao;

import io.vertx.ext.web.RoutingContext;

import java.util.regex.Pattern;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static io.vertx.core.json.Json.encode;

public class TransacaoHandler {

    private static final String APPLICATION_JSON = "application/json";
    private final TransacaoRepository repository;

    public TransacaoHandler(TransacaoRepository repository) {
        this.repository = repository;
    }

    public void criar(RoutingContext context) {
        var cliente = Long.parseLong(context.pathParam("id"));
        NovaTransacao novaTransacao;
        try {
            novaTransacao = context.body().asPojo(NovaTransacao.class);
        } catch (Exception ex) {
            context.response().setStatusCode(400).end();
            return;
        }
        if (verificarParametros(cliente, novaTransacao)) {
            context.response().setStatusCode(422).end();
            return;
        }
        var transacao = new Transacao(cliente, novaTransacao.valorLong(), novaTransacao.tipo(), novaTransacao.descricao());
        repository.criar(transacao)
                .onSuccess(saldo -> {
                    if (saldo.isEmpty()) {
                        context.response().setStatusCode(422).end();
                    } else {
                        context.response().putHeader(CONTENT_TYPE, APPLICATION_JSON).end(encode(saldo.get()));
                    }
                })
                .onFailure(ex -> context.response().setStatusCode(500).end());
    }

    private boolean verificarParametros(Long cliente, NovaTransacao transacao) {
        var pattern = Pattern.compile("^\\d+$");
        var descricao = transacao.descricao();
        var tipo = transacao.tipo();
        var valor = transacao.valor();
        return cliente == null || cliente < 1 || cliente > 5
                || descricao == null || descricao.length() > 10 || descricao.isEmpty()
                || tipo == null || !(tipo.equals("c") || tipo.equals("d"))
                || valor == null || !pattern.matcher(valor).matches();
    }
}
