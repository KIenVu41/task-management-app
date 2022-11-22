package com.kma.taskmanagement.ui.user;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.dialog.OTPVerificationDialog;
import com.kma.taskmanagement.ui.intro.IntroActivity;
import com.kma.taskmanagement.ui.main.MainActivity;
import com.kma.taskmanagement.ui.user.RegisterActivity;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @BindView(R.id.inputEmail)
    EditText edtEmail;
    @BindView(R.id.inputPassword)
    EditText edtPass;
    @BindView(R.id.gotoRegister)
    TextView gotoRegister;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.forgotPassword)
    TextView tvForgotPassword;
    @BindView(R.id.ivFinger)
    ImageView ivFinger;

    ProgressDialog progressDialog;

    private UserViewModel userViewModel;
    private UserRepository userRepository = new UserRepositoryImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        userViewModel =  new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        userViewModel.getProgress().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                if(value.equals("Thành công")) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                }
                progressDialog.setMessage(value);
                progressDialog.show();
            }
        });

       userViewModel.getResult().observe(this, new Observer<Token>() {
            @Override
            public void onChanged(Token token) {
                if(token != null) {
                    saveGlobalInfor(token);
                    SharedPreferencesUtil.getInstance(getApplicationContext()).storeUserToken(Constants.TOKEN + GlobalInfor.username, token.getToken() );
                    Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
                    startActivity(intent);
                }
            }
        });

       setOnclick();
       biometricEnable();
    }

    public void saveGlobalInfor(Token token) {
        GlobalInfor.id = token.getUserId();
        GlobalInfor.username = token.getUsername();
        GlobalInfor.sex = token.getSex();
        GlobalInfor.email = token.getEmail();
        GlobalInfor.phone = token.getPhone();
        GlobalInfor.url_image = token.getUrlImage();
    }

    public void setOnclick() {
        btnLogin.setOnClickListener(view -> {
            String username = edtEmail.getText().toString();
            String pass = edtPass.getText().toString();
            if(validateField(username, pass)) {
                userViewModel.login(username, pass);
            }
        });

        tvForgotPassword.setOnClickListener(view -> {
            OTPVerificationDialog otpVerificationDialog = new OTPVerificationDialog(this);
            otpVerificationDialog.setCancelable(true);
            otpVerificationDialog.show();
        });

        gotoRegister.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void biometricEnable() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        ivFinger.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }

    private boolean validateField(String username, String password) {
        if(username.trim().length() == 0) {
            Toast.makeText(this, "Chưa nhập username", Toast.LENGTH_SHORT).show();
            return false;
        } else if(username.trim().length() < 6) {
            Toast.makeText(this, "Username độ dài tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        } else if(password.trim().length() == 0) {
            Toast.makeText(this, "Chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        } else if(password.trim().length() < 8) {
            Toast.makeText(this, "Độ dài mật khẩu tối thiểu 8 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}