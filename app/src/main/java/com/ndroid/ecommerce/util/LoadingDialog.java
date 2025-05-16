package com.ndroid.ecommerce.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.ndroid.ecommerce.R;

public class LoadingDialog {
    private AlertDialog dialog;
    private final Context context;

    public LoadingDialog(Context context) {
        this.context = context;
    }

    public void show() {
        if (dialog != null && dialog.isShowing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    public void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
