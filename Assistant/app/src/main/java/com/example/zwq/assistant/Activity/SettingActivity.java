package com.example.zwq.assistant.Activity;

import androidx.constraintlayout.widget.ConstraintLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

public class SettingActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup rgStuType;
    private RadioButton rbStudent;
    private RadioButton rbAssistant;
    private ImageView ivReturn;
    private Button btnYes;
    private ConstraintLayout conEditPassWd;
    private ConstraintLayout conQuit;
    private int stuType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        initView();
    }

    public void initView(){
        btnYes = findViewById(R.id.btnYes);
        rgStuType = findViewById(R.id.rgStuType);
        rbStudent = findViewById(R.id.rbStudent);
        rbAssistant = findViewById(R.id.rbAssistant);
        ivReturn = findViewById(R.id.ivReturn);
        conEditPassWd = findViewById(R.id.conEditPassWd);
        conQuit = findViewById(R.id.conQuit);
        ivReturn.setOnClickListener(this);
        conEditPassWd.setOnClickListener(this);
        conQuit.setOnClickListener(this);
        btnYes.setOnClickListener(this);
        rgStuType.setOnCheckedChangeListener(this);
    }
    public void onClick(View view){
        super.onClick(view);
        Intent intent;
        switch (view.getId()){
            case R.id.conEditPassWd:
                intent = new Intent(this,EditPassActivity.class);
                startActivity(intent);
                break;
            case R.id.conQuit:
                intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.ivReturn:
                finish();
                break;
            case R.id.btnYes:
                RetrofitManager.getInstance().createReq(UserInfo.class)
                        .editStuType(UserInfoManager.getInstance().getUid(),stuType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<HttpResult<User>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(HttpResult<User> userHttpResult) {
                                if (userHttpResult.getCode() == 200){
                                    Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(SettingActivity.this,"切换成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(SettingActivity.this,"出现错误",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(SettingActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id){
            case R.id.rbStudent:
                stuType = 0;
                break;
            case R.id.rbAssistant:
                stuType = 2;
                break;
        }
    }
}
