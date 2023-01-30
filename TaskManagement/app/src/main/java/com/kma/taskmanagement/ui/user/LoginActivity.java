package com.kma.taskmanagement.ui.user;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.security.AES;
import com.kma.security.KeyPair;
import com.kma.security.StoreBackend;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.biometric.BiometricCallback;
import com.kma.taskmanagement.biometric.BiometricManager;
import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.Transaction;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.BaseActivity;
import com.kma.taskmanagement.ui.dialog.OTPVerificationDialog;
import com.kma.taskmanagement.ui.intro.IntroActivity;
import com.kma.taskmanagement.ui.main.MainActivity;
import com.kma.taskmanagement.ui.popup.Popup_AlertDialog;
import com.kma.taskmanagement.ui.user.RegisterActivity;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.Utils;
import com.scottyab.rootbeer.RootBeer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

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
//    @BindView(R.id.ivFinger)
//    ImageView ivFinger;

    ProgressDialog progressDialog;

    private UserViewModel userViewModel;
    private UserRepository userRepository = new UserRepositoryImpl();
    private String password = "";

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
                    SharedPreferencesUtil.getInstance(getApplicationContext()).storeUserToken(Constants.REFRESHTOKEN + GlobalInfor.username, token.getRefreshToken() );

                    Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

       //checkRoot();
        setOnclick();
        int timeout = getIntent().getIntExtra(GlobalInfor.actionTimeout, 1);
        if(timeout < 0) {
            showDialog();
        }
    }

    private void checkRoot() {
        RootBeer rootBeer = new RootBeer(this);
        if (rootBeer.isRooted()) {
            edtEmail.setEnabled(false);
            edtPass.setEnabled(false);
            btnLogin.setEnabled(false);
            tvForgotPassword.setOnClickListener(null);
            gotoRegister.setOnClickListener(null);
            Popup_AlertDialog.showDialogNotify(this,getResources().getString(R.string.rooted));
            btnLogin.setBackground(getResources().getDrawable(R.drawable.bg_gray_border));
        } else {
        }
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
            password = pass;
            if(validateField(username, pass)) {
                Utils.hideKeyboard(this);
                userViewModel.login(username, pass);
                SharedPreferencesUtil.getInstance(getApplicationContext()).storeStringInSharedPreferences(Constants.PASSWORD, pass);
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

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Đăng xuất");
        alertDialogBuilder.setMessage("Phiên đăng nhập đã hết hạn").setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
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