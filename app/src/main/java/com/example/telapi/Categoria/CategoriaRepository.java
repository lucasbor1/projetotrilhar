package com.example.telapi.Categoria;

import java.util.List;

public interface CategoriaRepository {
    List<String> listarCategorias();
    void adicionarCategoria(String nomeCategoria);
    void removerCategoria(String nomeCategoria);
    boolean categoriaJaExiste(String nomeCategoria);
}

