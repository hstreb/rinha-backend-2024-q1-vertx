# Solução para a Rinha de Backend 2024/Q1

Implementação para resolver o desafio da [Rinha de Backend 2024/Q1](https://github.com/zanfranceschi/rinha-de-backend-2024-q1).

Link para o repositório https://github.com/hstreb/rinha-backend-2024-q1-vertx.git

## Tecnologias

- java 21
- vertx 4.5
- PostgreSQL 16.2
- nginx

## Construir

- construir a aplicação:

    ```shell
    ./gradlew build
    ```

- construir o imagem docker

    ````shell
    docker build -t hstreb/rinha-2024-q1-vertx:0.0.1 .
    ````

- publicar a imagem no docker hub

    ````shell
    docker push hstreb/rinha-2024-q1-vertx:0.0.1
    ````

## Rodar

```shell
docker compose up -d
```