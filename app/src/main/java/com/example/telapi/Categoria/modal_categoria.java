package com.example.telapi.Categoria;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.telapi.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class modal_categoria extends DialogFragment {
    private EditText edtCategoria;
    private ImageButton btnAddCat;
    private ListView lvCategorias;
    private CategoriaDialogListener mListener;

    private CategoriaCRUD categoriaCRUD;
    private ArrayAdapter<String> categoriaAdapter;
    private String userId;

    public interface CategoriaDialogListener {
        void onCategoriaSelecionada(String nomeCategoria);
        void onCategoriaRemovida(String nomeCategoria);
        void onCategoriaAdicionada(String nomeCategoria);
    }

    public void setCategoriaDialogListener(CategoriaDialogListener listener) {
        this.mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categorias, container, false);
        inicializarComponentes(view);

        userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "default_user";
        categoriaCRUD = new CategoriaCRUD(requireContext(), userId);

        btnAddCat.setOnClickListener(v -> {
            String novaCategoria = edtCategoria.getText().toString().trim();
            adicionarCategoria(novaCategoria);
        });

        lvCategorias.setOnItemClickListener((parent, view1, position, id) -> {
            String categoriaSelecionada = categoriaAdapter.getItem(position);
            if (categoriaSelecionada != null && mListener != null) {
                mListener.onCategoriaSelecionada(categoriaSelecionada);
                dismiss();
            }
        });

        lvCategorias.setOnItemLongClickListener((parent, view1, position, id) -> {
            String categoria = categoriaAdapter.getItem(position);
            if (categoria != null) {
                exibirDialogoConfirmacaoExclusao(categoria);
            }
            return true;
        });

        carregarCategorias();
        return view;
    }

    private void inicializarComponentes(View view) {
        edtCategoria = view.findViewById(R.id.edtCategoria);
        btnAddCat = view.findViewById(R.id.btnAddCat);
        lvCategorias = view.findViewById(R.id.lvCategorias);

        categoriaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        lvCategorias.setAdapter(categoriaAdapter);
    }

    private void adicionarCategoria(String novaCategoria) {
        if (novaCategoria.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, insira o nome da categoria", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categoriaCRUD.categoriaJaExiste(novaCategoria)) {
            Toast.makeText(getContext(), "Categoria já existente", Toast.LENGTH_SHORT).show();
            return;
        }

        categoriaCRUD.adicionarCategoria(novaCategoria);
        edtCategoria.setText("");
        carregarCategorias();

        if (mListener != null) {
            mListener.onCategoriaAdicionada(novaCategoria);
        }
    }

    private void exibirDialogoConfirmacaoExclusao(String categoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Deseja realmente excluir a categoria '" + categoria + "'?")
                .setPositiveButton("Sim", (dialog, id) -> excluirCategoria(categoria))
                .setNegativeButton("Não", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    private void excluirCategoria(String categoria) {
        categoriaCRUD.removerCategoria(categoria);
        carregarCategorias();

        if (mListener != null) {
            mListener.onCategoriaRemovida(categoria);
        }
    }

    private void carregarCategorias() {
        List<String> categorias = categoriaCRUD.listarCategorias();
        if (categorias == null) {
            categorias = new ArrayList<>();
        }

        categoriaAdapter.clear();
        categoriaAdapter.addAll(categorias);
        categoriaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (categoriaCRUD != null) {
            categoriaCRUD.close();
        }
    }
}
