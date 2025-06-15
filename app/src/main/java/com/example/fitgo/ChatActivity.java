package com.example.fitgo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText    etMessage;
    private Button      btnSend;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();

    private String userId;     // cliente
    private String monitorId = "6gMJXUWwhZVGwTnlHeiHmLkVPlw1";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Referencias UI
        rvChat     = findViewById(R.id.rvChat);
        etMessage  = findViewById(R.id.etMessage);
        btnSend    = findViewById(R.id.btnSend);

        // Firestore + Auth
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // RecyclerView setup
        adapter = new ChatAdapter(messages, userId);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Escuchar mensajes en tiempo real
        db.collection("chats")
                .document(userId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) return;

                    messages.clear();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        String text   = doc.getString("text");
                        String sender = doc.getString("senderId");
                        com.google.firebase.Timestamp ts = doc.getTimestamp("timestamp");

                        // Si no hay timestamp, asignamos la hora actual o saltamos
                        Date fecha;
                        if (ts != null) {
                            fecha = ts.toDate();
                        } else {
                            fecha = new Date();  // o bien continue; para omitir este mensaje
                        }
                        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(fecha);

                        messages.add(new ChatMessage(sender, text, hora));
                    }

                    adapter.notifyDataSetChanged();
                    rvChat.scrollToPosition(messages.size() - 1);
                });

        // Enviar mensaje
        btnSend.setOnClickListener(v -> {
            String txt = etMessage.getText().toString().trim();
            if (txt.isEmpty()) return;
            Map<String,Object> msg = new HashMap<>();
            msg.put("senderId", userId);
            msg.put("text", txt);
            msg.put("timestamp", FieldValue.serverTimestamp());
            db.collection("chats")
                    .document(userId)
                    .collection("messages")
                    .add(msg);
            etMessage.setText("");
        });
    }
}
