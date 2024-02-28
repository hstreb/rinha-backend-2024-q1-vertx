package org.exemplo.rinha2024q1.extrato;

import java.time.Instant;

public record SaldoExtrato(Long total, Instant dataExtrato, Long limite) {
}
