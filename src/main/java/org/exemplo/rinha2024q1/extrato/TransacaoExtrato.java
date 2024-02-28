package org.exemplo.rinha2024q1.extrato;

import java.time.Instant;

public record TransacaoExtrato(Long valor, String tipo, String descricao, Instant realizadaEm) {
}