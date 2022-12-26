package com.kma.taskmanagement.ui.popup;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kma.taskmanagement.R;

public class Popup_AlertDialog {
    static Dialog dialog;
    static TextView tvMessage;
    static TextView tvOK;
    static boolean isShow = false;

    public static void showDialogNotify(Context ctActivity, String mess) {
        onDismiss();
        dialog = new Dialog(ctActivity, R.style.MyDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(ctActivity);
        View dialogView = inflater.inflate(R.layout.popup_alert, null);

        dialog.setContentView(dialogView);
        dialog.getWindow().setGravity(Gravity.CENTER);
        tvMessage = (TextView) dialogView.findViewById(R.id.tv_message);
        tvOK = (TextView) dialogView.findViewById(R.id.tv_OK);
        tvMessage.setText(mess);
        tvOK.setText(ctActivity.getResources().getString(R.string.OK));
        tvOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                isShow = false;
                onDismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);
    }

    public static void onDismiss() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

}
