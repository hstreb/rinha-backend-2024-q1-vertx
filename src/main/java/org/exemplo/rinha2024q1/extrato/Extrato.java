package org.exemplo.rinha2024q1.extrato;

import java.util.List;

public record Extrato(SaldoExtrato saldo, List<TransacaoExtrato> ultimasTransacoes) {
}
