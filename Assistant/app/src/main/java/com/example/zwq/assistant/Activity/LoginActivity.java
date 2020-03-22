package com.example.zwq.assistant.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.RetrofitManager;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
public class LoginActivity extends BaseActivity implements TextWatcher {

    EditText etPhone;
    EditText etPassWd;
    CheckBox cbPassWd;
    Button btnLogin;
    TextView tvRegister;
    TextView tvForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    public void initView(){
        etPhone = findViewById(R.id.etPhone);
        etPassWd = findViewById(R.id.etPassWd);
        cbPassWd = findViewById(R.id.cbPassWd);
        btnLogin = findViewById(R.id.btnLogin);
        tvForget = findViewById(R.id.tvForget);
        tvRegister = findViewById(R.id.tvRegister);

        etPhone.addTextChangedListener(this);
        etPassWd.addTextChangedListener(this);
        btnLogin.setOnClickListener(this);
        tvForget.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    public void onClick(View view){
        super.onClick(view);
        Intent intent;
        switch (view.getId()){
            case R.id.btnLogin:
                String account = etPhone.getText().toString();
                String pwd = etPassWd.getText().toString();
                RetrofitManager.getInstance()
                        .createReq(UserInfo.class)
                        .login(account,pwd)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<HttpResult<User>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(HttpResult<User> userHttpResult) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(LoginActivity.this,"用户名密码错误",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                break;
            case R.id.tvForget:

                break;
            case R.id.tvRegister:

                break;
        }

    }

    private void changeBtnBg() {
        if (etPassWd.getText().length()>6 && etPhone.getText().length()>10) {
            btnLogin.setBackgroundResource(R.drawable.shape_login);
        } else {
            btnLogin.setBackgroundResource(R.drawable.shape_login_no);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        changeBtnBg();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
