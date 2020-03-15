package com.tronix.kwakvoca;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

class WordActionDialog {

    private final String TAG = "WordActionDialog";
    private Context applicationContext;
    private LinearLayout background;
    private WordData data;

    WordActionDialog(Context applicationContext, LinearLayout background, WordData data) {
        this.applicationContext = applicationContext;
        this.background = background;
        this.data = data;
    }

    void show() {
        // Show dialog
        final Dialog dialog = new Dialog(applicationContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.word_action_dialog);
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // Set dialog's title as a selected word
        TextView wordView = dialog.findViewById(R.id.tv_title);
        wordView.setText(data.word);

        // Buttons (Actually LinearLayout)
        LinearLayout deleteWord = dialog.findViewById(R.id.btn_delete_word);
        LinearLayout copyWord = dialog.findViewById(R.id.btn_copy_word);
        LinearLayout editWord = dialog.findViewById(R.id.btn_edit);

        // Copy word button
        copyWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity mainActivity = new MainActivity();
                mainActivity.copyWord(applicationContext, background, data);
            }
        });

        // Delete word button
        deleteWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                DialogContents contents = new DialogContents();
                contents.title = data.word;
                contents.text = applicationContext.getString(R.string.dialog_warning_delete_word);
                contents.yes = applicationContext.getString(R.string.action_delete);
                contents.no = applicationContext.getString(R.string.action_cancel);
                contents.iconResId = R.drawable.ic_delete_white;

                DefaultDialog deleteDialog = new DefaultDialog(contents, DefaultDialog.DELETE_WORD,
                        applicationContext, background, data);
                deleteDialog.show();
            }
        });

        editWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity mainActivity = new MainActivity();
                mainActivity.moveToEditWord(data, applicationContext);
            }
        });
    }
}
