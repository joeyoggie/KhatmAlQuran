package com.joey.khatmalquran.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.joey.khatmalquran.R;

/**
 * Created by Joey on 11/25/2017.
 */

public class CustomProgressDialog extends Dialog {

    public static CustomProgressDialog show(Context context, CharSequence title,
                                            CharSequence message) {
        return show(context, title, message, false);
    }

    public static CustomProgressDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static CustomProgressDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static CustomProgressDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate,
                                            boolean cancelable, OnCancelListener cancelListener) {
        CustomProgressDialog dialog = new CustomProgressDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.setCanceledOnTouchOutside(false);
        /* The next line will add the ProgressBar to the dialog. */
        dialog.addContentView(new ProgressBar(context), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    public CustomProgressDialog(Context context) {
        super(context, R.style.CustomDialog);
    }
}
