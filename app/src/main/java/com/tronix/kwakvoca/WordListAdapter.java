package com.tronix.kwakvoca;

import android.content.Context;
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
    private Context applicationContext;
    private LinearLayout background;

    private final String TAG = "WordListAdapter";

    WordListAdapter(List<WordData> wordDataList, Context applicationContext, LinearLayout background) {
        this.wordDataList = wordDataList;
        this.applicationContext = applicationContext;
        this.background = background;
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
        final WordData wordData = wordDataList.get(position);

        final String word = wordData.word;
        final String meaning = wordData.meaning;
        final String documentId = wordData.documentId;

        holder.word.setText(word);
        holder.meaning.setText(meaning);

        holder.background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: clicked item=" + word + "  document id=" + documentId);

                DeleteWordDialog deleteWordDialog = new DeleteWordDialog(applicationContext, background, wordData);
                deleteWordDialog.show();

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
