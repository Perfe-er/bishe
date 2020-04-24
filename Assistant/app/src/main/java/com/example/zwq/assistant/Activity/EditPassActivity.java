package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

public class EditPassActivity extends BaseActivity implements TextWatcher {

    EditText etOldPwd;
    EditText etNewPwd;
    Button btnModify;
    ImageView ivReturn;
    private String oldPwd;
    private String newPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_pass);
        etOldPwd = findViewById(R.id.etOldPwd);
        etNewPwd = findViewById(R.id.etNewPwd);
        ivReturn = findViewById(R.id.ivReturn);
        btnModify = findViewById(R.id.btnModify);
        btnModify.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
        etNewPwd.addTextChangedListener(this);
        etOldPwd.addTextChangedListener(this);
    }

    public void onClick(View view){
        super.onClick(view);
        switch (view.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.btnModify:
                RetrofitManager.getInstance().createReq(UserInfo.class)
                        .modifyPassWd(UserInfoManager.getInstance().getUid(),oldPwd,newPwd)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<HttpResult<User>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(HttpResult<User> userHttpResult) {
                                if (userHttpResult.getCode() == 200){
                                    Toast.makeText(EditPassActivity.this,userHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EditPassActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(EditPassActivity.this,userHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(EditPassActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

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

    private void changeBtnBg() {
        oldPwd = etOldPwd.getText().toString();
        newPwd = etNewPwd.getText().toString();
        if (oldPwd != null && newPwd.length() >6) {
            btnModify.setBackgroundResource(R.drawable.shape_login);
            btnModify.setEnabled(true);
        } else {
            btnModify.setBackgroundResource(R.drawable.shape_login_no);
            btnModify.setEnabled(false);
        }
    }
}
