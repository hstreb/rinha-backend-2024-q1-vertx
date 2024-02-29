package org.exemplo.rinha2024q1.transacao;

import io.vertx.core.Future;
import io.vertx.pgclient.PgException;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.Optional;

public class TransacaoRepository {

    private final SqlClient sqlClient;

    public TransacaoRepository(SqlClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    public Future<Optional<Saldo>> criar(Transacao transacao) {
        return sqlClient.preparedQuery("SELECT t.saldo_novo, t.limite, t.erro FROM inserir_transcacao($1, $2, $3, $4) t")
                .execute(Tuple.of(transacao.cliente(),
                        transacao.tipo(),
                        transacao.valor(),
                        transacao.descricao()))
                .map(result -> {
                    var row = result.iterator().next();
                    if (row.getBoolean("erro")) {
                        return Optional.empty();
                    }
                    return Optional.of(new Saldo(row.getLong("limite"), row.getLong("saldo_novo")));
                });
    }
}