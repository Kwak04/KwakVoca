package com.tronix.kwakvoca;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import java.util.Objects;

class DeleteWordDialog {

    private Context applicationContext;

    DeleteWordDialog(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    void show() {
        Dialog dialog = new Dialog(applicationContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_word_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
