package com.example.telapi;


import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;



public class atv_metas extends AppCompatActivity {

        private CheckBox checkBoxMeta1;
        private CheckBox checkBoxMeta2;
        private CheckBox checkBoxMeta3;
        private CheckBox checkBoxMeta4;
        private CheckBox checkBoxMeta5;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.atv_metas);

            // Configura a Toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Remove o título

            // Habilita o botão de voltar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            if (getSupportActionBar() != null) {
                // Usa um ícone de tamanho maior do drawable
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationIcon(R.drawable.ic_botao_back_small ); // Substitua por seu ícone personalizado
            }



            checkBoxMeta1 = findViewById(R.id.checkBoxMeta1);
            checkBoxMeta2 = findViewById(R.id.checkBoxMeta2);
            checkBoxMeta3 = findViewById(R.id.checkBoxMeta3);
            checkBoxMeta4 = findViewById(R.id.checkBoxMeta4);
            checkBoxMeta5 = findViewById(R.id.checkBoxMeta5);

            checkBoxMeta1.setOnCheckedChangeListener(new MetaCheckedChangeListener("Meta 1"));
            checkBoxMeta2.setOnCheckedChangeListener(new MetaCheckedChangeListener("Meta 2"));
            checkBoxMeta3.setOnCheckedChangeListener(new MetaCheckedChangeListener("Meta 3"));
            checkBoxMeta4.setOnCheckedChangeListener(new MetaCheckedChangeListener("Meta 4"));
            checkBoxMeta5.setOnCheckedChangeListener(new MetaCheckedChangeListener("Meta 5"));
        }

        private class MetaCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
            private String metaName;

            MetaCheckedChangeListener(String metaName) {
                this.metaName = metaName;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(atv_metas.this, metaName + " realizada!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(atv_metas.this, metaName + " não realizada.", Toast.LENGTH_SHORT).show();
                }
            }
        }
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                if (item.getItemId() == android.R.id.home) {
                    // Finaliza a atividade atual e retorna à anterior
                    finish();
                    return true;
        }
        return super.onOptionsItemSelected(item);
        }
    }
