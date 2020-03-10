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
        TextView wordView = dialog.findViewById(R.id.tv_word);
        wordView.setText(data.word);

        // Buttons (Actually LinearLayout)
        LinearLayout deleteWord = dialog.findViewById(R.id.btn_delete_word);

        // Delete word button
        deleteWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DeleteWordDialog deleteWordDialog =
                        new DeleteWordDialog(applicationContext, background, data);
                deleteWordDialog.show();
            }
        });
    }
}
