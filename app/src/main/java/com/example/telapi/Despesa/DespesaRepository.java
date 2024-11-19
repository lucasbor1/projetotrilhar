package com.example.telapi.Despesa;

import java.util.List;

public interface DespesaRepository {
    void adicionarDespesa(Despesa despesa);
    void alterarDespesa(Despesa despesa);
    void removerDespesa(int id);
    List<Despesa> listarDespesas();
    List<Despesa> listarDespesasPorMesAno(String mes, int ano);
}
