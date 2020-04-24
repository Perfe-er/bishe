package com.example.zwq.assistant.Activity;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.LeaveInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Leave;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.text.SimpleDateFormat;

public class LeaveInfoActivity extends BaseActivity {
    TextView tvName;
    TextView tvSex;
    TextView tvClass;
    TextView tvStartTime;
    TextView tvEndTime;
    TextView tvRatify;
    TextView tvReason;
    Button btnNo;
    Button btnYes;
    ImageView ivReturn;
    ImageView ivPhone;
    private String phone;
    private int leaveID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_leave_info);
        initView();
        intentInfo();
    }

    public void initView(){
        tvName = findViewById(R.id.tvName);
        tvSex = findViewById(R.id.tvSex);
        tvClass = findViewById(R.id.tvClass);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvRatify = findViewById(R.id.tvRatify);
        tvReason = findViewById(R.id.tvReason);
        btnNo = findViewById(R.id.btnNo);
        btnYes = findViewById(R.id.btnYes);
        ivReturn = findViewById(R.id.ivReturn);
        ivPhone = findViewById(R.id.ivPhone);
        ivReturn.setOnClickListener(this);
        ivPhone.setOnClickListener(this);
        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.ivPhone:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone);
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.btnYes:
                agreeLeave();
                break;
            case R.id.btnNo:
                disagreeLeave();
                break;
        }
    }

    public void disagreeLeave(){
        RetrofitManager.getInstance().createReq(LeaveInfo.class)
                .ratifyLeave(leaveID,UserInfoManager.getInstance().getUid(),2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Leave>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Leave> leaveHttpResult) {
                        Toast.makeText(LeaveInfoActivity.this,leaveHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                        if (leaveHttpResult.getCode() == 200 ){
                            btnYes.setBackgroundResource(R.drawable.shape_login_no);
                            btnNo.setBackgroundResource(R.drawable.shape_login_no);
                            btnNo.setVisibility(View.GONE);
                            btnYes.setVisibility(View.GONE);
                            tvRatify.setText("不批准");
                        }else {
                            return;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void agreeLeave(){
        RetrofitManager.getInstance().createReq(LeaveInfo.class)
                .ratifyLeave(leaveID,UserInfoManager.getInstance().getUid(),1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Leave>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Leave> leaveHttpResult) {
                        Toast.makeText(LeaveInfoActivity.this,leaveHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                        if (leaveHttpResult.getCode() == 200 ){
                            btnYes.setBackgroundResource(R.drawable.shape_login_no);
                            btnNo.setBackgroundResource(R.drawable.shape_login_no);
                            btnNo.setVisibility(View.GONE);
                            btnYes.setVisibility(View.GONE);
                            tvRatify.setText("已批准");
                        }else {
                            return;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    public void intentInfo(){
        Intent intent = getIntent();
        leaveID = Integer.parseInt(intent.getStringExtra("leaveID"));
        phone = intent.getStringExtra("phone");
        int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
        if (userType == 2){
            btnNo.setVisibility(View.VISIBLE);
            btnYes.setVisibility(View.VISIBLE);
        }else {
            btnNo.setVisibility(View.GONE);
            btnYes.setVisibility(View.GONE);
        }
        tvClass.setText(intent.getStringExtra("className"));
        RetrofitManager.getInstance().createReq(LeaveInfo.class)
                .leveDetails(leaveID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Leave>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Leave> leaveHttpResult) {
                        if (leaveHttpResult.getCode() == 200 && leaveHttpResult.getData() != null){
                            int sex = leaveHttpResult.getData().getSex();
                            if (sex == 2){
                                tvSex.setText("男");
                            }else {
                                tvSex.setText("女");
                            }
                            int ratify = leaveHttpResult.getData().getRatify();
                            if (ratify == 0){
                                tvRatify.setText("待处理");
                                btnYes.setBackgroundResource(R.drawable.shape_login);
                                btnNo.setBackgroundResource(R.drawable.shape_login);
                                btnYes.setEnabled(true);
                                btnNo.setEnabled(true);
                            }else if (ratify == 1){
                                tvRatify.setText("已批准");
                                btnYes.setBackgroundResource(R.drawable.shape_login_no);
                                btnNo.setBackgroundResource(R.drawable.shape_login_no);
                                btnYes.setEnabled(false);
                                btnNo.setEnabled(false);
                            }else {
                                tvRatify.setText("不批准");
                                btnYes.setBackgroundResource(R.drawable.shape_login_no);
                                btnNo.setBackgroundResource(R.drawable.shape_login_no);
                                btnYes.setEnabled(false);
                                btnNo.setEnabled(false);
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                            long startTime1 = leaveHttpResult.getData().getStartDate();
                            String startTime = sdf.format(startTime1);
                            long endTime1 = leaveHttpResult.getData().getEndDate();
                            String endTime = sdf.format(endTime1);
                            tvName.setText(leaveHttpResult.getData().getName());
                            tvStartTime.setText(startTime);
                            tvEndTime.setText(endTime);
                            tvReason.setText(leaveHttpResult.getData().getReason());
                        }else {
                            return;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
