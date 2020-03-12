package com.tronix.kwakvoca;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

class DefaultDialog {

    static final int DEFAULT = 0;
    static final int DELETE_WORD = 1;

    private final String TAG = "DefaultDialog";

    private DialogContents contents;
    private int requestCode;
    private Context applicationContext;
    private LinearLayout background;
    private WordData data;

    // requestCode must be 'DEFAULT'
    DefaultDialog(DialogContents contents, int requestCode, Context applicationContext) {
        this.contents = contents;
        this.requestCode = requestCode;
        this.applicationContext = applicationContext;
    }

    // requestCode must be 'DELETE_WORD'
    DefaultDialog(DialogContents contents, int requestCode, Context applicationContext,
                  LinearLayout background, WordData data) {
        this.contents = contents;
        this.requestCode = requestCode;
        this.applicationContext = applicationContext;
        this.background = background;
        this.data = data;
    }

    void show() {
        // Show dialog
        final Dialog dialog = new Dialog(applicationContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.default_dialog);
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();  // Show the dialog

        // Set icon
        ImageView icon = dialog.findViewById(R.id.image_icon);
        icon.setImageResource(contents.iconResId);

        // Set dialog's title
        TextView title = dialog.findViewById(R.id.tv_title);
        title.setText(contents.title);

        // Set dialog's text
        TextView text = dialog.findViewById(R.id.tv_text);
        text.setText(contents.text);

        // Buttons
        TextView no = dialog.findViewById(R.id.btn_no);
        TextView yes = dialog.findViewById(R.id.btn_yes);

        // Cancel button
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Set buttons' text
        no.setText(contents.no);
        yes.setText(contents.yes);


        // Operate differently according to requestCode

        if (requestCode == DEFAULT) {
            no.setText(null);
            no.setEnabled(false);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (requestCode == DELETE_WORD) {
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();  // First dismiss the dialog
                    Log.d(TAG,"onDeleteClick: word=" + data.word
                            + " documentId=" + data.documentId);
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.deleteWord(data, background);
                }
            });
        }
    }
}
