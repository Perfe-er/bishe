package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

public class OtherInfoActivity extends AppCompatActivity {
    TextView tvNumber;
    TextView tvName;
    TextView tvStuID;
    TextView tvCollege;
    TextView tvClass;
    TextView tvBirthday;
    TextView tvStuType;
    TextView tvPhone;
    TextView tvParentPho;
    TextView tvIDCard;
    TextView tvAddress;
    ImageView ivSex;
    ImageView ivReturn;
    ImageView ivHead;
    ConstraintLayout conAddress;
    ConstraintLayout conInfo;
    ConstraintLayout conNumber;
    ConstraintLayout conStuID;
    ConstraintLayout conIDCard;
    ConstraintLayout conClass;
    ConstraintLayout conStuType;
    ConstraintLayout conParentPho;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_other_info);
        initView();
        getUserInfo();
        authority();
    }

    public void initView() {
        tvName = findViewById(R.id.tvName);
        tvStuID = findViewById(R.id.tvStuID);
        tvNumber = findViewById(R.id.tvNumber);
        tvCollege = findViewById(R.id.tvCollege);
        tvClass = findViewById(R.id.tvClass);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvStuType = findViewById(R.id.tvStuType);
        tvPhone = findViewById(R.id.tvAssistantPhone);
        tvParentPho = findViewById(R.id.tvPerentPho);
        tvIDCard = findViewById(R.id.tvIDCard);
        tvAddress = findViewById(R.id.tvAddress);
        ivSex = findViewById(R.id.ivSex);
        ivReturn = findViewById(R.id.ivReturn);
        ivHead = findViewById(R.id.ivHead);
        conInfo = findViewById(R.id.conInfo);
        conNumber = findViewById(R.id.conNumber);
        conAddress = findViewById(R.id.conAddress);
        conStuID = findViewById(R.id.conStuID);
        conStuType = findViewById(R.id.conStuType);
        conClass = findViewById(R.id.conClass);
        conParentPho = findViewById(R.id.conParentPho);
        conIDCard = findViewById(R.id.conIDCard);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        conStuType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
                if (userType == 2){
                    selectStuType();
                }else {
                    Toast.makeText(OtherInfoActivity.this,"你不是导员，无权修改",Toast.LENGTH_SHORT).show();
                }
            }
        });
        conNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherInfoActivity.this, OtherMoralInfoActivity.class);
                intent.putExtra("userID",userID + "");
                startActivity(intent);
            }
        });
    }

    public void selectStuType(){
        new AlertDialog.Builder(this).setTitle("修改身份")
                .setPositiveButton("班委", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stuType(1);
                    }
                }).setNegativeButton("普通学生", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stuType(0);
                    }
                }).show();
    }

    private void stuType(int stuType) {
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .editStuType(userID, stuType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200) {
                            if(userHttpResult.getData().getStuType() == 1){
                                tvStuType.setText("班委");
                            }else if (userHttpResult.getData().getStuType() == 0){
                                tvStuType.setText("普通学生");
                            }
                            Toast.makeText(OtherInfoActivity.this, "切换成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OtherInfoActivity.this, "出现错误", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(OtherInfoActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    private void authority(){
        int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
        if (userType == 2 ){
            conAddress.setVisibility(View.VISIBLE);
            conIDCard.setVisibility(View.VISIBLE);
            conParentPho.setVisibility(View.VISIBLE);
        }else{
            conAddress.setVisibility(View.GONE);
            conIDCard.setVisibility(View.GONE);
            conParentPho.setVisibility(View.GONE);
        }
    }
    private void getUserInfo() {
        Intent intent = getIntent();
        userID = Integer.parseInt(intent.getStringExtra("userID"));
        RetrofitManager.getInstance()
                .createReq(UserInfo.class)
                .getUserInfoById(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() != null) {
                            tvName.setText(userHttpResult.getData().getName());
                            tvStuID.setText(userHttpResult.getData().getStuID());
                            tvAddress.setText(userHttpResult.getData().getAddress());
                            tvCollege.setText(userHttpResult.getData().getCollege());
                            tvClass.setText(userHttpResult.getData().getClassName());
                            tvIDCard.setText(userHttpResult.getData().getIdentity());
                            tvPhone.setText(userHttpResult.getData().getPhone());
                            tvClass.setText(userHttpResult.getData().getClassName());
                            tvParentPho.setText(userHttpResult.getData().getParentPho());
                            String head = userHttpResult.getData().getHead();
                            Glide.with(OtherInfoActivity.this)
                                    .load(head)
                                    .into(ivHead);
                            double number = userHttpResult.getData().getNumber();
                            tvNumber.setText(String.valueOf(number));
                            int sex = userHttpResult.getData().getSex();
                            if (sex == 1){
                                ivSex.setImageResource(R.drawable.sex_g);
                            }else {
                                ivSex.setImageResource(R.drawable.sex_m);
                            }
                            tvBirthday.setText(userHttpResult.getData().getBirthday());
                            int stuType = userHttpResult.getData().getStuType();
                            if (stuType == 0 ){
                                tvStuType.setText("普通学生");
                                conStuID.setVisibility(View.VISIBLE);
                                conNumber.setVisibility(View.VISIBLE);
                                conClass.setVisibility(View.VISIBLE);
                            }else if (stuType == 1){
                                tvStuType.setText("班委");
                                conStuID.setVisibility(View.VISIBLE);
                                conNumber.setVisibility(View.VISIBLE);
                                conClass.setVisibility(View.VISIBLE);
                            }else if (stuType == 2){
                                tvStuType.setText("导员");
                                conStuID.setVisibility(View.GONE);
                                conNumber.setVisibility(View.GONE);
                                conClass.setVisibility(View.GONE);
                            }
                        }else {
                            Toast.makeText(OtherInfoActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(OtherInfoActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
