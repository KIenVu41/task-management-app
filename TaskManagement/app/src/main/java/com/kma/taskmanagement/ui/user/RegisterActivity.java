package com.kma.taskmanagement.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.security.StoreBackend;
import com.kma.security.utils.Constants;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.remote.RetrofitInstance;
import com.kma.taskmanagement.data.remote.UserService;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.main.MainActivity;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.TextUtils;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.edtEmail)
    EditText inputEmail;
    @BindView(R.id.edtPhone)
    EditText inputPhone;
    @BindView(R.id.edtUserName)
    EditText inputUsername;
    @BindView(R.id.edtPassword)
    EditText inputPassword;
    @BindView(R.id.btnRegister)
    Button btnRegister;
//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;
    @BindView(R.id.gotoLogin)
    TextView gotoLogin;
    ProgressDialog progressDialog;

    private UserViewModel userViewModel;
    private UserRepository userRepository = new UserRepositoryImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        userViewModel =  new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.getProgress().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String value) {
                progressDialog.setMessage(value);
                if(value.equals("Thành công")) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        userViewModel.getRegisterResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("TAG", s);
            }
        });

        register();
        gotoLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void register() {
        btnRegister.setOnClickListener(view -> {
            String email = inputEmail.getText().toString();
            String phone = inputPhone.getText().toString();
            String username = inputUsername.getText().toString();
            String password = inputPassword.getText().toString();
            if(validateFields(email, phone, username, password)) {
                //progressDialog.show();
                //userViewModel.singup(new RegisterRequest(email, 0, password, phone, "", username));
                enroll();
            }
        });
    }

    private boolean validateFields(String email, String phone, String username, String password) {
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
        } else if(email.trim().length() == 0) {
            Toast.makeText(this, "Chưa nhập email", Toast.LENGTH_SHORT).show();
            return false;
        } else if(phone.trim().length() == 0) {
            Toast.makeText(this, "Chưa nhập phone", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!TextUtils.isValidEmail(email)) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!phone.startsWith("0")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void enroll() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            PublicKey publicKey = keyStore.getCertificate(Constants.KEY_NAME).getPublicKey();
            KeyFactory factory = KeyFactory.getInstance(publicKey.getAlgorithm());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey.getEncoded());
            PublicKey verificationKey = factory.generatePublic(spec);
            Log.d("TAG", verificationKey.toString());
            StoreBackend.enroll(1, verificationKey);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException |
                IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

}