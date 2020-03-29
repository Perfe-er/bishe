package com.example.zwq.assistant.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.UserInfoManager;
import com.mob.MobSDK;

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
    final String REMEMBER_PWD_PREF = "rememberPwd";
    final String ACCOUNT_PREF = "account";
    final String PASSWORD_PREF = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MobSDK.submitPolicyGrantResult(true, null);
        initView();

        final SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = preference.getBoolean(REMEMBER_PWD_PREF, false);
        if (isRemember) {
            etPhone.setText(preference.getString(ACCOUNT_PREF, ""));
            etPassWd.setText(preference.getString(PASSWORD_PREF, ""));
            cbPassWd.setChecked(true);
        }
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
        final Intent intent;
        switch (view.getId()){
            case R.id.btnLogin:
                final SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
                final String account = etPhone.getText().toString();
                final String pwd = etPassWd.getText().toString();
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
                                if (userHttpResult.getCode() == 200){
                                    SharedPreferences.Editor editor = preference.edit();
                                    if (cbPassWd.isChecked()) {//记住账号与密码
                                        editor.putBoolean(REMEMBER_PWD_PREF, true);
                                        editor.putString(ACCOUNT_PREF,account);
                                        editor.putString(PASSWORD_PREF, pwd);
                                    } else {//清空数据
                                        editor.clear();
                                    }
                                    editor.apply();
                                    UserInfoManager.getInstance().onLogin(userHttpResult.getData());
                                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(LoginActivity.this,TabMenuActivity.class);
                                    startActivity(intent1);

                                }else {
                                    Toast.makeText(LoginActivity.this,userHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(LoginActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                break;
            case R.id.tvForget:

                break;
            case R.id.tvRegister:
                    intent = new Intent(this,RegisterActivity.class);
                    startActivity(intent);
                break;
        }

    }

    private void changeBtnBg() {
        if (etPassWd.getText().length()>6 && etPhone.getText().length()>10) {
            btnLogin.setBackgroundResource(R.drawable.shape_login);
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setBackgroundResource(R.drawable.shape_login_no);
            btnLogin.setEnabled(false);
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
