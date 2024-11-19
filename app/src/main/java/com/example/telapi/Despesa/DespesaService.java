package com.example.telapi.Despesa;

import java.util.List;

public class DespesaService {
    private final DespesaRepository despesaRepository;

    public DespesaService(DespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
    }

    public void salvarOuAtualizarDespesa(Despesa despesa, boolean isAtualizar) {
        if (isAtualizar) {
            despesaRepository.alterarDespesa(despesa);
        } else {
            despesaRepository.adicionarDespesa(despesa);
        }
    }

    public void removerDespesa(int id) {
        despesaRepository.removerDespesa(id);
    }

    public List<Despesa> listarDespesasPorMesAno(String mes, int ano) {
        return despesaRepository.listarDespesasPorMesAno(mes, ano);
    }
}
