package com.kma.taskmanagement.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.intro.IntroActivity;
import com.kma.taskmanagement.ui.main.MainActivity;
import com.kma.taskmanagement.ui.user.RegisterActivity;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

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
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private UserViewModel userViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private UserRepository userRepository = new UserRepositoryImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        userViewModel =  new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        userViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                progressBar.setVisibility(visibility);
            }
        });

       userViewModel.getLoginResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("TAG", s);
                if(!s.equals("")) {
                    SharedPreferencesUtil.getInstance(getApplicationContext()).storeUserToken(Constants.TOKEN +"kienvu41", s );
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnLogin.setOnClickListener(view -> {
//            mDisposable.add(userViewModel.login(new LoginRequest(pass, username))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(token -> onHandleSucces(token) ,
//                            throwable -> Log.e("", "Unable to register", throwable)));
//                    .subscribe(new SingleObserver<Token>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onSuccess(Token token) {
//                            if (!token.getToken().equals("")) {
//                                Log.d("TAG", token.getToken());
//                                SharedPreferencesUtil.getInstance(getApplicationContext()).storeUserToken(Constants.TOKEN +"kienvu41", token.getToken() );
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(intent);
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Log.d("TAG", "err" + e.getMessage());
//                        }
//                    }));
            String username = edtEmail.getText().toString();
            String pass = edtPass.getText().toString();
            userViewModel.login(username, pass);
        });

        gotoRegister.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    public void onHandleSucces(Token token) {
        if (!token.getToken().equals("")) {
            Log.d("TAG", token.getToken());
            SharedPreferencesUtil.getInstance(getApplicationContext()).storeUserToken(Constants.TOKEN +"kienvu41", token.getToken() );
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void saveGlobakInfor(User user) {
        GlobalInfor.id = user.getId();
        GlobalInfor.username = user.getUsername();
        GlobalInfor.password = user.getPassword();
        GlobalInfor.sex = user.getSex();
        GlobalInfor.email = user.getEmail();
        GlobalInfor.phone = user.getPhone();
        GlobalInfor.url_image = user.getUrl_image();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // clear all the subscriptions
        mDisposable.clear();
    }
}