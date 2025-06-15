package com.example.fitgo;

// ChatAdapter.java
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {

    private final List<ChatMessage> items;
    private final String me;

    public ChatAdapter(List<ChatMessage> items, String myUid) {
        this.items = items;
        this.me    = myUid;
    }

    @Override public int getItemViewType(int pos) {
        return items.get(pos).senderId.equals(me)
                ? R.layout.item_chat_sent
                : R.layout.item_chat_received;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ChatMessage m = items.get(pos);
        h.tvText.setText(m.text);
        h.tvTime.setText(m.time);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvText, tvTime;
        VH(@NonNull View v) {
            super(v);
            tvText = v.findViewById(R.id.tvMsg);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }
}
