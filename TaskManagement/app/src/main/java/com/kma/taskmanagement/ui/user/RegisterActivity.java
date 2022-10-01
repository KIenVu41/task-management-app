package com.kma.taskmanagement.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.LoginRequest;
import com.kma.taskmanagement.data.model.RegisterRequest;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.remote.RetrofitInstance;
import com.kma.taskmanagement.data.remote.UserService;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.inputEmail)
    EditText inputEmail;
    @BindView(R.id.inputPhone)
    EditText inputPhone;
    @BindView(R.id.inputUserName)
    EditText inputUsername;
    @BindView(R.id.inputPassword)
    EditText inputPassword;
//    @BindView(R.id.btnRegister)
    AppCompatButton btnRegister;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private UserViewModel userViewModel;
//    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private UserRepository userRepository = new UserRepositoryImpl();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        btnRegister = findViewById(R.id.btnRegister);
//        inputEmail.setText("test@gmail.com");
//        inputPassword.setText("kocopass");
//        inputPhone.setText("0955930566");
//        inputUsername.setText("test");

        userViewModel =  new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        register();
    }

    private void register() {
        String email = inputEmail.getText().toString();
        String phone = inputPhone.getText().toString();
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

//        RetrofitInstance.getRetrofitInstance().create(UserService.class).signUp(new LoginRequest("kvtrung20@gmail.com", 0,"password", "0944395928", "", "kvu20"))
//                .enqueue(new Callback<LoginRequest>() {
//                    @Override
//                    public void onResponse(Call<LoginRequest> call, Response<LoginRequest> response) {
//                        Log.d("TAG",  "resp" + response.body().getEmail());
//                    }
//
//                    @Override
//                    public void onFailure(Call<LoginRequest> call, Throwable t) {
//                        Log.d("TAG",  "onFail" + t.getMessage());
//                    }
//                });

        btnRegister.setOnClickListener(view -> {
            //showProgress();
            //hideKeyboard();
//            mDisposable.add(userViewModel.singup(new RegisterRequest(email, 0,password, phone, "", username))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(() -> Log.d("TAG", "sucesss"),
//                            throwable -> Log.e("", "Unable to register", throwable)));
            userViewModel.singup(new RegisterRequest(email, 0, password, phone, "", username), new UserViewModel.IRegisterResponse() {
                @Override
                public void onResponse() {
                    Log.d("TAG", "Success");
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("TAG", "Fail " + t.getMessage());
                }
            });
        });
    }

    public void showProgress() {
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.onProcess)); // Setting Message
        progressDialog.setTitle(""); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        // clear all the subscriptions
//        mDisposable.clear();
//    }

    public void hideKeyboard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            }
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(),
                    0
            );
        }

    }
}