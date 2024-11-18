package com.example.telapi.Categoria;

import com.example.telapi.Categoria.CategoriaRepository;

import java.util.List;

public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<String> listarCategorias() {
        return categoriaRepository.listarCategorias();
    }

    public List<String> obterCategorias() {
        return categoriaRepository.listarCategorias();
    }

    public void adicionarCategoria(String categoria) {
        if (!categoriaRepository.categoriaJaExiste(categoria)) {
            categoriaRepository.adicionarCategoria(categoria);
        }
    }
}
