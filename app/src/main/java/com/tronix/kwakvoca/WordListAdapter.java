package com.tronix.kwakvoca;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {
    private List<WordData> wordDataList;

    WordListAdapter(List<WordData> wordDataList) {
        this.wordDataList = wordDataList;
    }

    @NonNull
    @Override
    public WordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListAdapter.ViewHolder holder, int position) {
        final String word = wordDataList.get(position).word;
        final String meaning = wordDataList.get(position).meaning;
        final String documentId = wordDataList.get(position).documentId;

        holder.word.setText(word);
        holder.meaning.setText(meaning);

        holder.background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("WordListAdapter", "onLongClick: clicked item=" + word + "  document id=" + documentId);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordDataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout background;
        TextView word, meaning;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            background = itemView.findViewById(R.id.layout_item);
            word = itemView.findViewById(R.id.tv_word);
            meaning = itemView.findViewById(R.id.tv_meaning);
        }
    }
}
