package com.example.unlost.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unlost.R;
import com.example.unlost.entities.Note;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    ArrayList<Note> notesList;
    ItemClick activity;

    public interface ItemClick{
        void onClick(int index);
    }

    public NotesAdapter(ArrayList<Note> notesList, Context context) {
        this.notesList = notesList;
        activity=(ItemClick) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nTitle, nContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nTitle= itemView.findViewById(R.id.note_title);
            nContent=itemView.findViewById(R.id.note_content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onClick(notesList.indexOf((Note)v.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(notesList.get(position));
        holder.nTitle.setText(notesList.get(position).getTitle());
        holder.nContent.setText(notesList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}