package org.exemplo.rinha2024q1.transacao;

public record NovaTransacao(String valor, String tipo, String descricao) {
    public Long valorLong() {
        return Long.parseLong(valor);
    }
}
