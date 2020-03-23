package com.tronix.kwakvoca;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
    public void onBindViewHolder(@NonNull WordListAdapter.ViewHolder holder, final int position) {
        final WordData wordData = wordDataList.get(position);

        final String word = wordData.word;
        final List<String> meanings = wordData.meanings;
        final String documentId = wordData.documentId;

        StringBuilder builder = new StringBuilder();
        String space = "";
        String meaningEach;

        for (int i = 0; i < meanings.size(); i++) {
            if (meanings.size() > 1) {
                meaningEach = space + (i + 1) + ". " + meanings.get(i);
            } else {
                meaningEach = meanings.get(i);
            }
            builder.append(meaningEach);
            space = "\n";
        }

        holder.word.setText(word);
        holder.meaning.setText(builder.toString());

        holder.background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: clicked item=" + word + "  document id=" + documentId);

                WordActionDialog wordActionDialog =
                        new WordActionDialog(applicationContext, background, wordData);
                wordActionDialog.show();

                return true;
            }
        });

        final CheckBox bookmark = holder.bookmark;
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "checked=" + bookmark.isChecked() + "  position=" + position);

                MainActivity mainActivity = new MainActivity();
                wordData.isBookmarked = bookmark.isChecked();
                mainActivity.bookmarkWord(wordData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout background;
        TextView word, meaning;
        CheckBox bookmark;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            background = itemView.findViewById(R.id.layout_item);
            word = itemView.findViewById(R.id.tv_title);
            meaning = itemView.findViewById(R.id.tv_meaning);
            bookmark = itemView.findViewById(R.id.cb_bookmark);
        }
    }
}
