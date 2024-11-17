package com.example.telapi.Categoria;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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
    private FirebaseAuth auth;

    public interface CategoriaDialogListener {
        void onCategoriaAdicionada(String categoria);
        void onCategoriaRemovida(String categoriaId);
    }

    public void setCategoriaDialogListener(CategoriaDialogListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categorias, container, false);
        edtCategoria = view.findViewById(R.id.edtCategoria);
        btnAddCat = view.findViewById(R.id.btnAddCat);
        lvCategorias = view.findViewById(R.id.lvCategorias);

        auth = FirebaseAuth.getInstance();
        categoriaCRUD = new CategoriaCRUD();

        btnAddCat.setOnClickListener(v -> {
            String novaCategoria = edtCategoria.getText().toString();
            adicionarCategoria(novaCategoria);
        });

        categoriaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        lvCategorias.setAdapter(categoriaAdapter);
        lvCategorias.setOnItemClickListener((parent, view1, position, id) -> {
            String categoria = categoriaAdapter.getItem(position);
            if (categoria != null && mListener != null) {
                mListener.onCategoriaAdicionada(categoria);
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

    private void adicionarCategoria(String novaCategoria) {
        if (!novaCategoria.isEmpty()) {
            categoriaCRUD.adicionarCategoria(novaCategoria);
            edtCategoria.setText("");
            carregarCategorias();

            if (mListener != null) {
                mListener.onCategoriaAdicionada(novaCategoria);
            }
            dismiss();
        } else {
            Toast.makeText(getContext(), "Por favor, insira o nome da categoria", Toast.LENGTH_SHORT).show();
        }
    }


    private void exibirDialogoConfirmacaoExclusao(String categoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Deseja realmente excluir a categoria '" + categoria + "'?")
                .setPositiveButton("Sim", (dialog, id) -> excluirCategoria(categoria))
                .setNegativeButton("Não", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void excluirCategoria(String categoria) {
        categoriaCRUD.removerCategoria(categoria);
        carregarCategorias();
    }

    private void carregarCategorias() {
        categoriaCRUD.listarCategorias(new CategoriaCRUD.CategoriasCallback() {
            @Override
            public void onCategoriasLoaded(List<String> categorias) {
                categoriaAdapter.clear();
                categoriaAdapter.addAll(categorias);
                categoriaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCategoriasFailed(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
