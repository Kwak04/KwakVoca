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
    private String word;
    private String documentId;

    private final String TAG = "DeleteWordDialog";

    DeleteWordDialog(Context applicationContext, LinearLayout background, String word, String documentId) {
        this.applicationContext = applicationContext;
        this.background = background;
        this.word = word;
        this.documentId = documentId;
    }

    void show() {
        // Show dialog
        final Dialog dialog = new Dialog(applicationContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_word_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

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
                Log.d(TAG, "onDeleteClick: word=" + word + " documentId=" + documentId);
                MainActivity mainActivity = new MainActivity();
                mainActivity.deleteWord(documentId, background);
            }
        });
    }
}
