FROM ubuntu:22.04

COPY build/native/nativeCompile/rinha-2024-q1-vertx app

CMD ["./app"]
