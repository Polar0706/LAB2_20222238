package com.example.lab2_20222238_iot;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab2_20222238_iot.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupSpinner();
        setupButtons();
    }

    private void setupSpinner() {
        String[] opciones = {"Sí", "No"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTexto.setAdapter(adapter);

        binding.spinnerTexto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    binding.tilEscribirTexto.setVisibility(View.VISIBLE);
                } else {
                    binding.tilEscribirTexto.setVisibility(View.GONE);
                    binding.etEscribirTexto.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupButtons() {
        binding.btnComprobarConexion.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                Toast.makeText(this, "✓ Conexión a internet disponible", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "✗ Sin conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnComenzar.setOnClickListener(v -> {
            if (validateInputs()) {
                if (isInternetAvailable()) {
                    startTeleCatActivity();
                } else {
                    Toast.makeText(this, "Error Toast", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInputs() {
        String cantidad = binding.etCantidad.getText().toString().trim();
        if (cantidad.isEmpty()) {
            Toast.makeText(this, "Debe ingresar una cantidad", Toast.LENGTH_SHORT).show();
            return false;
        }

        int cantidadNum;
        try {
            cantidadNum = Integer.parseInt(cantidad);
            if (cantidadNum <= 0) {
                Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return false;
        }

        String textoOption = binding.spinnerTexto.getSelectedItem().toString();
        if ("Sí".equals(textoOption)) {
            String escribirTexto = binding.etEscribirTexto.getText().toString().trim();
            if (escribirTexto.isEmpty()) {
                Toast.makeText(this, "Debe escribir el texto", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void startTeleCatActivity() {
        Intent intent = new Intent(this, TeleCatActivity.class);
        intent.putExtra("cantidad", Integer.parseInt(binding.etCantidad.getText().toString().trim()));
        
        String textoOption = binding.spinnerTexto.getSelectedItem().toString();
        intent.putExtra("incluirTexto", "Sí".equals(textoOption));
        
        if ("Sí".equals(textoOption)) {
            intent.putExtra("textoPersonalizado", binding.etEscribirTexto.getText().toString().trim());
        }
        
        startActivity(intent);
    }
}