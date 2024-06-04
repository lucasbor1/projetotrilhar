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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class modal_categoria extends DialogFragment {
    private EditText edtCategoria;
    private ImageButton btnAddCat;
    private ListView lvCategorias;
    private CategoriaDialogListener mListener;

    private FirebaseFirestore db;
    private ArrayAdapter<String> categoriaAdapter;

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

        db = FirebaseFirestore.getInstance();


        btnAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String novaCategoria = edtCategoria.getText().toString();
                adicionarCategoria(novaCategoria);
            }
        });

        // Configurar a lista de categorias
        categoriaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        lvCategorias.setAdapter(categoriaAdapter);
        lvCategorias.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String categoria = categoriaAdapter.getItem(position);
                exibirDialogoConfirmacaoExclusao(categoria);
                return true;
            }
        });

        carregarCategorias();
        return view;
    }

    private void adicionarCategoria(String novaCategoria) {
        if (!novaCategoria.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("nome", novaCategoria);

            db.collection("categorias").add(data)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Categoria adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                        edtCategoria.setText(""); // Limpar o campo de texto após adicionar
                        carregarCategorias(); // Atualizar a lista de categorias no AutoCompleteTextView
                        if (mListener != null) {
                            mListener.onCategoriaAdicionada(novaCategoria);
                        }
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao adicionar categoria", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Por favor, insira o nome da categoria", Toast.LENGTH_SHORT).show();
        }
    }

    private void exibirDialogoConfirmacaoExclusao(String categoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Deseja realmente excluir a categoria '" + categoria + "'?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        excluirCategoria(categoria);
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void excluirCategoria(String categoria) {
        db.collection("categorias")
                .whereEqualTo("nome", categoria)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Categoria removida com sucesso!", Toast.LENGTH_SHORT).show();
                                    carregarCategorias(); // Recarregar a lista de categorias
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Erro ao remover categoria", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao encontrar categoria", Toast.LENGTH_SHORT).show();
                });
    }

    private void carregarCategorias() {
        db.collection("categorias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> categorias = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String nomeCategoria = document.getString("nome");
                        categorias.add(nomeCategoria);
                    }
                    categoriaAdapter.clear();
                    categoriaAdapter.addAll(categorias);
                    categoriaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao carregar categorias", Toast.LENGTH_SHORT).show();
                });
    }

}
