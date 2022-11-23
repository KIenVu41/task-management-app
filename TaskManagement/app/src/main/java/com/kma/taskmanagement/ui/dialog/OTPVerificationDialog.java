package com.kma.taskmanagement.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.utils.GlobalInfor;

public class OTPVerificationDialog extends Dialog {

    private EditText otp1, otp2, otp3, otp4;
    private TextView tvResend, tvOtpEmail;
    private Button btnVerify;
    private boolean resendEnabled = false;
    private int resendTime = 60;
    private int selected = 0;

    public OTPVerificationDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
        setContentView(R.layout.layout_otp);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        tvResend = findViewById(R.id.tvResend);
        tvOtpEmail = findViewById(R.id.tvOtpEmail);
        btnVerify = findViewById(R.id.btnVerify);

        otp1.addTextChangedListener(textWatcher);
        otp2.addTextChangedListener(textWatcher);
        otp3.addTextChangedListener(textWatcher);
        otp4.addTextChangedListener(textWatcher);
        if(GlobalInfor.email.length() != 0) {
            tvOtpEmail.setText(GlobalInfor.email);
        }
        showKeyBoard(otp1);

        startCountDownTime();

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resendEnabled) {
                    startCountDownTime();
                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getOtp = otp1.getText().toString()+otp2.getText().toString()+otp3.getText().toString()+otp4.getText().toString();

                if(getOtp.length() == 4) {

                }
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length() > 0) {
                if(selected == 0) {
                    selected = 1;
                    showKeyBoard(otp2);
                } else if(selected == 1) {
                    selected = 2;
                    showKeyBoard(otp3);
                } else if(selected == 2) {
                    selected = 3;
                    showKeyBoard(otp4);
                } else {
                    btnVerify.setBackgroundColor(R.drawable.round_back_red);
                }
            }
        }
    };

    private void showKeyBoard(EditText edtOtp) {
        edtOtp.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(edtOtp, InputMethodManager.SHOW_IMPLICIT);

    }

    private void startCountDownTime() {
        resendEnabled = false;
        tvResend.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime * 1000, 1000) {

            @Override
            public void onTick(long l) {
                tvResend.setText("Gửi lại mã (" + (l / 1000) + ")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                tvResend.setText("Gửi lại mã");
                tvResend.setTextColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
            }
        }.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL) {
            if(selected == 3) {
                selected = 2;
                showKeyBoard(otp3);
            } else if(selected == 2) {
                selected = 1;
                showKeyBoard(otp2);
            } else if(selected == 1) {
                selected = 0;
                showKeyBoard(otp1);
            }

            btnVerify.setBackgroundResource(R.drawable.round_back_brown);
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
}