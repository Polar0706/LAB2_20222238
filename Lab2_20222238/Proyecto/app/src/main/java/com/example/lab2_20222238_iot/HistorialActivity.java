package com.example.lab2_20222238_iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20222238_iot.databinding.ActivityHistorialBinding;

public class HistorialActivity extends AppCompatActivity {

    private ActivityHistorialBinding binding;
    private static final String PREFS_NAME = "TeleCatPrefs";
    private static final String KEY_INTERACCIONES = "interacciones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistorialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Historial");
        }

        setupUI();
        loadHistorial();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_historial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_limpiar_historial) {
            showClearHistorialDialog();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupUI() {
        binding.btnVolverAJugar.setOnClickListener(v -> showConfirmationDialog());
    }

    private void loadHistorial() {
        binding.llHistorialContainer.removeAllViews();
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String interacciones = prefs.getString(KEY_INTERACCIONES, "");

        if (interacciones.isEmpty()) {
            binding.tvSinHistorial.setVisibility(View.VISIBLE);
            return;
        }

        binding.tvSinHistorial.setVisibility(View.GONE);
        String[] interaccionesArray = interacciones.split(",");
        
        for (int i = 0; i < interaccionesArray.length; i++) {
            TextView textView = createInteractionTextView(i + 1, interaccionesArray[i]);
            binding.llHistorialContainer.addView(textView);
        }
    }

    private TextView createInteractionTextView(int numero, String cantidad) {
        TextView textView = new TextView(this);
        textView.setText("Interacción " + numero + ": " + cantidad + " imágenes");
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(R.color.text_primary, null));
        textView.setPadding(16, 12, 16, 12);
        
        return textView;
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Confirmación")
            .setMessage("¿Está seguro que desea volver a Jugar?")
            .setPositiveButton("Sí", (dialog, which) -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
            .create()
            .show();
    }

    private void showClearHistorialDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Limpiar Historial")
            .setMessage("¿Está seguro que desea limpiar todo el historial?")
            .setPositiveButton("Sí", (dialog, which) -> {
                clearHistorial();
                loadHistorial();
            })
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
            .create()
            .show();
    }

    private void clearHistorial() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().remove(KEY_INTERACCIONES).apply();
    }
}