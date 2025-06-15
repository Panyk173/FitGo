package com.example.fitgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ExerciseAdapter extends BaseAdapter {

    public interface RemoveListener {
        void onRemove(int position);
    }

    private final Context context;
    private List<Exercise> exerciseList;
    private final RemoveListener removeListener;

    public ExerciseAdapter(Context context, List<Exercise> exercises, RemoveListener listener) {
        this.context = context;
        this.exerciseList = exercises;
        this.removeListener = listener;
    }

    @Override
    public int getCount() {
        return exerciseList.size();
    }

    @Override
    public Object getItem(int position) {
        return exerciseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
            holder = new ViewHolder();
            holder.imgGif = convertView.findViewById(R.id.imgGif);
            holder.tvName = convertView.findViewById(R.id.tvNameEs);
            holder.tvTarget = convertView.findViewById(R.id.tvTargetEs);
            holder.btnRemove = convertView.findViewById(R.id.btnRemove);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Exercise e = exerciseList.get(position);

        Glide.with(context)
                .asGif()
                .load(e.getGifUrl())
                .into(holder.imgGif);

        holder.tvName.setText(e.getNameEs() != null ? e.getNameEs() : e.getName());
        holder.tvTarget.setText(e.getTargetEs() != null ? e.getTargetEs() : e.getTarget());

        if (removeListener != null) {
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnRemove.setOnClickListener(v -> removeListener.onRemove(position));
        } else {
            holder.btnRemove.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void updateList(List<Exercise> newList) {
        this.exerciseList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView imgGif;
        TextView tvName, tvTarget;
        ImageButton btnRemove;
    }
}
