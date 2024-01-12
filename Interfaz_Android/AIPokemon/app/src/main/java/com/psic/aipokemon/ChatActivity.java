package com.psic.aipokemon;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.psic.aipokemon.core.src.Battle;
import com.psic.aipokemon.core.src.Player;

import java.util.ArrayList;
import java.util.Map;

public class ChatActivity extends ComponentActivity {
    public ScrollView chatScrollView;
    public TextView chatTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatScrollView = findViewById(R.id.logScrollView);
        chatTextView = findViewById(R.id.logTextView);

        Button battButton = findViewById(R.id.battleButton);
        battButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, BattleActivity.class);
                startActivity(intent);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<String> valorRecibido = extras.getStringArrayList("chat");
            printLogMessages(valorRecibido);
        }
    }

    private void printLogMessages(ArrayList<String> messages) {
        // Obtener el contenido actual de la TextView
        String currentContent = chatTextView.getText().toString();

        // Iterar sobre los mensajes y agregar cada uno al contenido
        for (String message : messages) {
            currentContent += "\n" + message;
        }

        // Actualizar el contenido de la TextView
        chatTextView.setText(currentContent);

        // Desplazar el ScrollView hacia abajo para mostrar el mensaje más reciente
        chatScrollView.post(new Runnable() {
            @Override
            public void run() {
                chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}