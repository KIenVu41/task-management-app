package com.kma.taskmanagement.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.data.remote.request.ChangePassRequest;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.user.LoginActivity;
import com.kma.taskmanagement.ui.user.UserViewModel;
import com.kma.taskmanagement.ui.user.UserViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

import java.util.concurrent.TimeUnit;

public class OTPVerificationDialog extends Dialog {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6, edtOtpPhone, edtPass, edtRePass;
    private TextView tvResend, tvSend;
    private Button btnVerify;
    private boolean resendEnabled = false;
    private int resendTime = 60;
    private int selected = 0;
    private FirebaseAuth mAuth;
    private String verificationId = "";
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    Activity activity;
    private UserViewModel userViewModel;
    private UserRepository userRepository = new UserRepositoryImpl();
    private String token = "";

    public OTPVerificationDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
        setContentView(R.layout.layout_otp);

        userViewModel =  new ViewModelProvider((ViewModelStoreOwner) activity, new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        edtOtpPhone = findViewById(R.id.edtOtpPhone);
        edtPass = findViewById(R.id.edtPass);
        edtRePass = findViewById(R.id.edtRePass);
        tvResend = findViewById(R.id.tvResend);
        tvSend = findViewById(R.id.tvSend);
        btnVerify = findViewById(R.id.btnVerify);

        mAuth = FirebaseAuth.getInstance();

        otp1.addTextChangedListener(textWatcher);
        otp2.addTextChangedListener(textWatcher);
        otp3.addTextChangedListener(textWatcher);
        otp4.addTextChangedListener(textWatcher);
        otp5.addTextChangedListener(textWatcher);
        otp6.addTextChangedListener(textWatcher);
        showKeyBoard(otp1);

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = edtOtpPhone.getText().toString().trim();
                if(phone.isEmpty()) {
                    Toast.makeText(getContext(), "Chưa nhập sđt", Toast.LENGTH_SHORT).show();
                    return;
                }
                onClickVerifyPhoneNumber(phone);
                startCountDownTime();
            }
        });

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = edtOtpPhone.getText().toString().trim();
                if(resendEnabled) {
                    startCountDownTime();
                    onClickVerifyPhoneNumber(phone);
                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getOtp = otp1.getText().toString()+otp2.getText().toString()+otp3.getText().toString()+otp4.getText().toString()+otp5.getText().toString()+otp6.getText().toString();

                if(resendEnabled) {
                    Toast.makeText(getContext(), "Mã hết hạn, vui lòng nhập mã mới", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(getOtp.length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, getOtp);
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(getContext(), "Chưa nhập mã", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onClickVerifyPhoneNumber(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(100L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)
                        .setForceResendingToken(mForceResendingToken)// Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationId = s;
                                mForceResendingToken = forceResendingToken;
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            token = SharedPreferencesUtil.getInstance(activity.getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
                            if(edtPass.getText().toString().trim().equals(edtRePass.getText().toString().trim())) {
                                userViewModel.changePass(Constants.BEARER + token, new ChangePassRequest("kocopass123", edtPass.getText().toString().trim()));
                            } else {
                                Toast.makeText(getContext(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getContext(), "Mã xác thực không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void resendOtp(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
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
                } else if(selected == 3) {
                    selected = 4;
                    showKeyBoard(otp5);
                } else if(selected == 4) {
                    selected = 5;
                    showKeyBoard(otp6);
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
        btnVerify.setBackgroundResource(R.drawable.round_blue);

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
                btnVerify.setBackgroundResource(R.drawable.round_back_brown);
            }
        }.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL) {
            if(selected == 5) {
                selected = 4;
                showKeyBoard(otp5);
            } else if(selected == 4) {
                selected = 3;
                showKeyBoard(otp4);
            } else if(selected == 3) {
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
