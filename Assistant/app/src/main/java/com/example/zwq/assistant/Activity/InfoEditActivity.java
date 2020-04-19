package com.example.zwq.assistant.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Objects;

import androidx.constraintlayout.widget.ConstraintLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class InfoEditActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    private EditText etName;
    private EditText etStuID;
    private EditText etParentPho;
    private EditText etIDCard;
    private EditText etAddress;
    private EditText etCollege;
    private EditText etBirthday;
    private ImageView ivReturn;
    private Button btnSave;
    private ConstraintLayout conStuID;
    private ConstraintLayout conParentPho;
    private ConstraintLayout conIDCard;
    private ConstraintLayout conAddress;
    private RadioButton rbSexM;
    private RadioButton rbSexG;
    private RadioGroup rgSex;
    private int sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info_edit);
        initView();
        intentInfo();
    }

    public void initView(){
        etName = findViewById(R.id.etName);
        etStuID = findViewById(R.id.etStuID);
        etParentPho = findViewById(R.id.etParentPho);
        etIDCard = findViewById(R.id.etIDCard);
        etAddress = findViewById(R.id.etAddress);
        etCollege = findViewById(R.id.etCollege);
        etBirthday = findViewById(R.id.etBirthday);
        conStuID = findViewById(R.id.conStuID);
        conParentPho = findViewById(R.id.conPerentPho);
        conIDCard = findViewById(R.id.conIDCard);
        conAddress = findViewById(R.id.conAddress);
        rgSex = findViewById(R.id.rgSex);
        rbSexM = findViewById(R.id.rbSexM);
        rbSexG = findViewById(R.id.rbSexG);
        ivReturn = findViewById(R.id.ivReturn);
        btnSave = findViewById(R.id.btnSave);
        rgSex.setOnCheckedChangeListener(this);
        ivReturn.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    public void onClick(View view){
        super.onClick(view);
        switch (view.getId()){
            case R.id.btnSave:
                String stuID = etStuID.getText().toString();
                String name = etName.getText().toString();
                String college = etCollege.getText().toString();
                String parentPho = etParentPho.getText().toString();
                String IDCard = etIDCard.getText().toString();
                String address = etAddress.getText().toString();
                String birthday = etBirthday.getText().toString();

                RetrofitManager.getInstance()
                        .createReq(UserInfo.class)
                        .infoEdit(UserInfoManager.getInstance().getUid(),stuID,name, sex, college,
                                parentPho, IDCard,address,birthday)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<HttpResult<User>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(HttpResult<User> userHttpResult) {
                                if (userHttpResult.getCode() == 200){
                                    UserInfoManager.getInstance().onLogin(userHttpResult.getData());
                                    Toast.makeText(InfoEditActivity.this,userHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    Toast.makeText(InfoEditActivity.this,"出现错误，修改失败",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(InfoEditActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

                break;
            case R.id.ivReturn:
                finish();
                break;
        }
    }


    public void intentInfo(){
        Intent intent = getIntent();
        etName.setText(intent.getStringExtra("name"));
        etStuID.setText(intent.getStringExtra("stuID"));
        etCollege.setText(intent.getStringExtra("college"));
        etBirthday.setText(intent.getStringExtra("birthday"));
        etAddress.setText(intent.getStringExtra("address"));
        etIDCard.setText(intent.getStringExtra("IDCard"));
        etParentPho.setText(intent.getStringExtra("parentPho"));
        String stuType =intent.getStringExtra("stuType");
        if (stuType.equals("导员")){
            conStuID.setVisibility(View.GONE);
            conAddress.setVisibility(View.GONE);
            conParentPho.setVisibility(View.GONE);
            conIDCard.setVisibility(View.GONE);
        }else {
            conStuID.setVisibility(View.VISIBLE);
            conAddress.setVisibility(View.VISIBLE);
            conParentPho.setVisibility(View.VISIBLE);
            conIDCard.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id){
            case R.id.rbSexG:
                sex = 1;
                break;
            case R.id.rbSexM:
                sex = 2;
                break;
        }
    }
}
