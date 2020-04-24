package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

public class MineActivity extends AppCompatActivity {
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
    ImageView ivSetting;
    ImageView ivHead;
    ConstraintLayout conAddress;
    ConstraintLayout conInfo;
    ConstraintLayout conNumber;
    ConstraintLayout conStuID;
    ConstraintLayout conIDCard;
    ConstraintLayout conClass;
    ConstraintLayout conParentPho;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mine);
        initView();
        getUserInfo();
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
        conClass = findViewById(R.id.conClass);
        conParentPho = findViewById(R.id.conPerentPho);
        conIDCard = findViewById(R.id.conIDCard);
        ivSetting = findViewById(R.id.ivSetting);
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineActivity.this,InfoEditActivity.class);
                intent.putExtra("name",tvName.getText());
                intent.putExtra("stuID",tvStuID.getText());
                intent.putExtra("college",tvCollege.getText());
                intent.putExtra("birthday",tvBirthday.getText());
                intent.putExtra("parentPho",tvParentPho.getText());
                intent.putExtra("address",tvAddress.getText());
                intent.putExtra("IDCard",tvAddress.getText());
                intent.putExtra("stuType",tvStuType.getText());
                startActivity(intent);
                startActivity(intent);
            }
        });
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUserInfo() {
        int userID = UserInfoManager.getInstance().getUid();
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
                            }else if (stuType == 1){
                                tvStuType.setText("班委");
                            }else if (stuType == 2){
                                tvStuType.setText("导员");
                            }
                            String url = userHttpResult.getData().getHead();
                            Glide.with(MineActivity.this)
                                    .load(url)
                                    .into(ivHead);

                        }else {
                            Toast.makeText(MineActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MineActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
