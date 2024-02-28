package org.exemplo.rinha2024q1.extrato;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.util.stream.StreamSupport.stream;

public class ExtratoRepository {

    private final SqlClient sqlClient;

    public ExtratoRepository(SqlClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    private static SaldoExtrato mapear(Row row) {
        return new SaldoExtrato(row.getLong("saldo"), OffsetDateTime.now(ZoneOffset.UTC).toInstant(), row.getLong("limite"));
    }

    private static List<TransacaoExtrato> mapear(RowSet<Row> rowSet) {
        return stream(rowSet.spliterator(), false)
                .map(row -> new TransacaoExtrato(row.getLong("valor"),
                        row.getString("tipo"),
                        row.getString("descricao"),
                        row.getLocalDateTime("realizada_em").toInstant(ZoneOffset.UTC)))
                .toList();
    }

    public Future<Extrato> consultar(Long cliente) {
        return sqlClient.preparedQuery("SELECT saldo, limite FROM clientes WHERE id = $1")
                .execute(Tuple.of(cliente))
                .map(rowSet -> {
                    var row = rowSet.iterator().next();
                    return new Extrato(mapear(row), List.of());
                })
                .flatMap(extrato -> sqlClient.preparedQuery("""
                                SELECT valor, tipo, descricao, realizada_em FROM transacoes 
                                WHERE cliente_id = $1 ORDER BY realizada_em DESC LIMIT 10
                                """)
                        .execute(Tuple.of(cliente))
                        .map(ExtratoRepository::mapear)
                        .map(transacoes -> new Extrato(extrato.saldo(), transacoes)));
    }
}
