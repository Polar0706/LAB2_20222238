package com.example.lab2_20222238_iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lab2_20222238_iot.databinding.ActivityTelecatBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeleCatActivity extends AppCompatActivity {

    private ActivityTelecatBinding binding;
    private int cantidad;
    private boolean incluirTexto;
    private String textoPersonalizado;
    private int imagenActual = 0;
    private CountDownTimer timer;
    private ExecutorService executor;
    private Handler mainHandler;
    private static final String PREFS_NAME = "TeleCatPrefs";
    private static final String KEY_INTERACCIONES = "interacciones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelecatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        setupUI();
        setupExecutor();
        loadFirstImage();
        startTimer();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        cantidad = intent.getIntExtra("cantidad", 1);
        incluirTexto = intent.getBooleanExtra("incluirTexto", false);
        textoPersonalizado = intent.getStringExtra("textoPersonalizado");
    }

    private void setupUI() {
        binding.tvCantidad.setText("Cantidad = " + cantidad);
        
        binding.btnSiguiente.setOnClickListener(v -> {
            if (imagenActual < cantidad - 1) {
                imagenActual++;
                loadNextImage();
                restartTimer();
                binding.btnSiguiente.setEnabled(false);
            } else {
                finishActivity();
            }
        });
    }

    private void setupExecutor() {
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void loadFirstImage() {
        loadCatImage();
    }

    private void loadNextImage() {
        loadCatImage();
    }

    private void loadCatImage() {
        executor.execute(() -> {
            String imageUrl = buildImageUrl();
            
            mainHandler.post(() -> {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.ivCatImage);
            });
        });
    }

    private String buildImageUrl() {
        String baseUrl = "https://cataas.com/cat";
        
        if (incluirTexto && textoPersonalizado != null && !textoPersonalizado.isEmpty()) {
            return baseUrl + "/says/" + textoPersonalizado.replace(" ", "%20");
        }
        
        return baseUrl + "?t=" + System.currentTimeMillis();
    }

    private void startTimer() {
        long tiempoTotal = (long) cantidad * 125;
        
        timer = new CountDownTimer(tiempoTotal, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                
                String timeString = String.format("%02d:%02d", minutes, seconds);
                binding.tvTiempoRestante.setText(timeString);
            }

            @Override
            public void onFinish() {
                binding.tvTiempoRestante.setText("00:00");
                binding.btnSiguiente.setEnabled(true);
            }
        };
        
        timer.start();
    }

    private void restartTimer() {
        if (timer != null) {
            timer.cancel();
        }
        startTimer();
    }

    private void finishActivity() {
        saveInteraction();
        
        Intent intent = new Intent(this, HistorialActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveInteraction() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String interacciones = prefs.getString(KEY_INTERACCIONES, "");
        
        if (!interacciones.isEmpty()) {
            interacciones += ",";
        }
        interacciones += cantidad;
        
        prefs.edit().putString(KEY_INTERACCIONES, interacciones).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }
}