package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ContactActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageView ivSend;

    private ChatAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();

    private FirebaseFirestore db;
    private String userId;
    private ListenerRegistration chatListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Inicializar vistas
        rvChat     = findViewById(R.id.rvChat);
        etMessage  = findViewById(R.id.etMessage);
        ivSend     = findViewById(R.id.ivSend);

        // Logo que vuelve al HomeActivity
        ImageView logoIcon = findViewById(R.id.logo_icon);
        if (logoIcon != null) {
            logoIcon.setOnClickListener(v -> {
                Intent intent = new Intent(ContactActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        // Setup RecyclerView
        adapter = new ChatAdapter(messages, FirebaseAuth.getInstance().getCurrentUser().getUid());
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Firestore & Auth
        db     = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Listener en tiempo real
        chatListener = db.collection("chats")
                .document(userId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(this::onChatEvent);

        // Enviar mensaje
        ivSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty()) return;
            Map<String,Object> msg = new HashMap<>();
            msg.put("senderId", userId);
            msg.put("text", text);
            msg.put("timestamp", FieldValue.serverTimestamp());

            db.collection("chats")
                    .document(userId)
                    .collection("messages")
                    .add(msg);
            etMessage.setText("");
        });

        // Footer navegación (idéntica a tu código original)
        findViewById(R.id.ivHealth).setOnClickListener(v ->
                startActivity(new Intent(this, HealthActivity.class)));
        findViewById(R.id.ivWeight).setOnClickListener(v ->
                startActivity(new Intent(this, WeightActivity.class)));
        findViewById(R.id.ivRoutine).setOnClickListener(v ->
                startActivity(new Intent(this, RoutineActivity.class)));
        findViewById(R.id.ivSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
        // ivContact no hace nada porque ya estamos en esta pantalla
    }

    private void onChatEvent(@Nullable QuerySnapshot snap, @Nullable FirebaseFirestoreException e) {
        if (e != null || snap == null) return;
        messages.clear();

        for (DocumentSnapshot doc : snap.getDocuments()) {
            String sender = doc.getString("senderId");
            String text   = doc.getString("text");
            Timestamp ts  = doc.getTimestamp("timestamp");

            Date date = ts!=null ? ts.toDate() : new Date();
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(date);

            messages.add(new ChatMessage(sender, text, time));
        }

        adapter.notifyDataSetChanged();
        rvChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatListener != null) chatListener.remove();
    }
}
