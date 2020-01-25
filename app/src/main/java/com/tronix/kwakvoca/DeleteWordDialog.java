package com.tronix.kwakvoca;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

class DeleteWordDialog {

    private Context applicationContext;
    private LinearLayout background;
    private WordData data;

    private final String TAG = "DeleteWordDialog";

    DeleteWordDialog(Context applicationContext, LinearLayout background, WordData data) {
        this.applicationContext = applicationContext;
        this.background = background;
        this.data = data;
    }

    void show() {
        // Show dialog
        final Dialog dialog = new Dialog(applicationContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_word_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();  // Show the dialog

        // Set dialog's title as a selected word
        TextView wordView = dialog.findViewById(R.id.tv_word);
        wordView.setText(data.word);

        // Buttons
        TextView cancel = dialog.findViewById(R.id.btn_cancel);
        TextView delete = dialog.findViewById(R.id.btn_delete);

        // Cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();  // First dismiss the dialog
                Log.d(TAG, "onDeleteClick: word=" + data.word + " documentId=" + data.documentId);
                MainActivity mainActivity = new MainActivity();
                mainActivity.deleteWord(data, background);
            }
        });
    }
}
