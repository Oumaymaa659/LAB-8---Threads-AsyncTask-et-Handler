package com.example.labthreadsasynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Identifiants camouflés
    private TextView label_info;
    private ProgressBar loader_bar;
    private ImageView display_image;
    private Handler uiThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On lie le layout XML
        setContentView(R.layout.activity_main);

        // Initialisation des composants avec les nouveaux IDs du XML
        initViewElements();

        uiThreadHandler = new Handler(Looper.getMainLooper());

        // Gestionnaire de clic pour le bouton Toast
        findViewById(R.id.action_show_toast).setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "L'interface est fluide !", Toast.LENGTH_SHORT).show()
        );

        // Clic pour le Thread classique
        findViewById(R.id.run_thread_btn).setOnClickListener(v -> startImageFetch());

        // Clic pour l'AsyncTask
        findViewById(R.id.run_async_btn).setOnClickListener(v -> new BackgroundProcessor().execute());
    }

    private void initViewElements() {
        label_info = findViewById(R.id.label_info);
        loader_bar = findViewById(R.id.loader_bar);
        display_image = findViewById(R.id.display_image);
    }

    // --- PARTIE 1 : THREAD & HANDLER ---
    private void startImageFetch() {
        loader_bar.setVisibility(View.VISIBLE);
        label_info.setText("Récupération via Thread...");

        new Thread(() -> {
            // Simulation d'un délai réseau (1.5 seconde)
            try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }

            // Chargement de l'icône
            final Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

            // Retour sur l'UI Thread via le Handler
            uiThreadHandler.post(() -> {
                display_image.setImageBitmap(icon);
                loader_bar.setVisibility(View.GONE);
                label_info.setText("Image chargée avec succès");
            });
        }).start();
    }

    // --- PARTIE 2 : ASYNCTASK CAMOUFLÉE ---
    private class BackgroundProcessor extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            loader_bar.setVisibility(View.VISIBLE);
            loader_bar.setProgress(0);
            label_info.setText("Calcul complexe en cours...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            for (int step = 1; step <= 100; step++) {
                try {
                    // Simulation de calcul intense
                    Thread.sleep(30);
                } catch (InterruptedException e) { e.printStackTrace(); }

                publishProgress(step);
            }
            return "Opération terminée !";
        }

        @Override
        protected void onProgressUpdate(Integer... progressValues) {
            loader_bar.setProgress(progressValues[0]);
        }

        @Override
        protected void onPostExecute(String msg) {
            loader_bar.setVisibility(View.GONE);
            label_info.setText(msg);
            Toast.makeText(MainActivity.this, "Tâche AsyncTask réussie", Toast.LENGTH_SHORT).show();
        }
    }
}