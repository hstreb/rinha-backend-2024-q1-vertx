package org.exemplo.rinha2024q1;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import org.exemplo.rinha2024q1.extrato.ExtratoHandler;
import org.exemplo.rinha2024q1.extrato.ExtratoRepository;
import org.exemplo.rinha2024q1.transacao.TransacaoHandler;
import org.exemplo.rinha2024q1.transacao.TransacaoRepository;

public class App extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final int INSTANCIAS = Integer.parseInt(System.getenv().getOrDefault("INSTANCIAS", "2"));

    static {
        DatabindCodec.mapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    private final int httpPort = Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT", "8080"));
    private final int poolMaxSize = Integer.parseInt(System.getenv().getOrDefault("POOL_MAX_SIZE", "4"));

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(App::new, new DeploymentOptions().setInstances(INSTANCIAS));
    }

    private static ExtratoHandler getExtratoHandler(SqlClient sqlClient) {
        var extratoRepository = new ExtratoRepository(sqlClient);
        return new ExtratoHandler(extratoRepository);
    }

    private static TransacaoHandler getTransacaoHandler(SqlClient sqlClient) {
        var transacaoRepository = new TransacaoRepository(sqlClient);
        return new TransacaoHandler(transacaoRepository);
    }

    @Override
    public void start(Promise<Void> start) {
        var sqlClient = getSqlClient();
        var transacaoHandler = getTransacaoHandler(sqlClient);
        var extratoHandler = getExtratoHandler(sqlClient);

        var router = Router.router(vertx);
        router.get("/health").respond(ctx -> Future.succeededFuture());
        router.post("/clientes/:id/transacoes")
                .consumes("application/json")
                .handler(BodyHandler.create())
                .handler(transacaoHandler::criar);
        router.get("/clientes/:id/extrato")
                .handler(extratoHandler::consultar);
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(httpPort)
                .onSuccess(ok -> {
                    LOGGER.info("Aplicação rodando na porta " + httpPort);
                    start.complete();
                })
                .onFailure(failure -> {
                    LOGGER.error("Aplicacao falhou ao iniciar", failure);
                    start.fail(failure);
                });
    }

    private SqlClient getSqlClient() {
        var connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setHost(System.getenv().getOrDefault("DB_HOST", "localhost"))
                .setDatabase(System.getenv().getOrDefault("DB_NAME", "rinha"))
                .setUser(System.getenv().getOrDefault("DB_USER", "rinha"))
                .setPassword(System.getenv().getOrDefault("DB_PASSWORD", "rinha123"));
        var poolOptions = new PoolOptions()
                .setMaxSize(poolMaxSize);
        return PgBuilder
                .client()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(vertx)
                .build();
    }
}
