package com.example.zwq.assistant.Activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ForgetPassWordActivity extends BaseActivity implements TextWatcher {

    EditText etPhone;
    EditText etPassWd;
    EditText etAreaCode;
    Button btnAreaCode;
    Button btnModify;

    EventHandler eventHandler;
    private boolean flag=true;
    private String phoneNumber;
    private String cordNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word);
        initView();
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg=new Message();
                msg.arg1=event;
                msg.arg2=result;
                msg.obj=data;
                handler.sendMessage(msg);
            }
        };

        SMSSDK.registerEventHandler(eventHandler);
    }

    public void initView(){
        etPhone = findViewById(R.id.etPhone);
        etPassWd = findViewById(R.id.etPassWd);
        etAreaCode = findViewById(R.id.etAreaCode);
        btnAreaCode = findViewById(R.id.btnAreaCode);
        btnModify = findViewById(R.id.btnModify);
        btnAreaCode.setOnClickListener(this);
        btnModify.setOnClickListener(this);
        etPhone.addTextChangedListener(this);
        etPassWd.addTextChangedListener(this);
        etAreaCode.addTextChangedListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAreaCode:
                if(judPhone())//去掉左右空格获取字符串
                {
                    final ForgetPassWordActivity.MyCountDownTimer myCountDownTimer = new ForgetPassWordActivity.MyCountDownTimer(60000,1000);
                    myCountDownTimer.start();
                    //请求验证码
                    SMSSDK.getVerificationCode("86",phoneNumber);
                    etAreaCode.requestFocus();
                }
                break;
            case R.id.btnModify:
                if(judCord())
                    //提交验证码
                    SMSSDK.submitVerificationCode("86",phoneNumber,cordNumber);
                flag=false;
                if(judCord()&&judPhone()){
                    phoneNumber = etPhone.getText().toString();
                    String passNumber = etPassWd.getText().toString();
                    RetrofitManager.getInstance().createReq(UserInfo.class)
                            .modifyPassWdByPhone(UserInfoManager.getInstance().getUid(),phoneNumber,passNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<HttpResult<User>>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(HttpResult<User> userHttpResult) {
                                    if (userHttpResult.getCode() == 200 && eventHandler.equals(SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE)){
                                        Toast.makeText(ForgetPassWordActivity.this,userHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ForgetPassWordActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(ForgetPassWordActivity.this,userHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(ForgetPassWordActivity.this,"网络出错",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }

                break;
            default:
                break;
        }
    }


    /**
     * 核对手机号
     * @return
     */
    private boolean judPhone()
    {
        if(TextUtils.isEmpty(etPhone.getText().toString().trim()))
        {
            Toast.makeText(this,"请输入您的电话号码",Toast.LENGTH_LONG).show();
            etPhone.requestFocus();
            return false;
        }
        else if(etPhone.getText().toString().trim().length()!=11)
        {
            Toast.makeText(this,"您的电话号码位数不正确",Toast.LENGTH_LONG).show();
            etPhone.requestFocus();
            return false;
        }
        else
        {
            phoneNumber=etPhone.getText().toString().trim();
            String num="[1][358]\\d{9}";
            if(phoneNumber.matches(num))
                return true;
            else
            {
                Toast.makeText(this,"请输入正确的手机号码",Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }

    /**
     * 对输入的验证码核对
     * @return
     */
    private boolean judCord()
    {
        judPhone();
        if(TextUtils.isEmpty(etAreaCode.getText().toString().trim()))
        {
            Toast.makeText(this,"请输入您的验证码",Toast.LENGTH_LONG).show();
            etAreaCode.requestFocus();
            return false;
        }
        else if(etAreaCode.getText().toString().trim().length()!=6)
        {
            Toast.makeText(this,"您的验证码位数不正确",Toast.LENGTH_LONG).show();
            etAreaCode.requestFocus();

            return false;
        }
        else
        {
            cordNumber=etAreaCode.getText().toString().trim();
            return true;
        }

    }

    /**
     * 验证码计时器
     */
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            btnAreaCode.setClickable(false);
            btnAreaCode.setBackgroundResource(R.drawable.shape_login_no);
            btnAreaCode.setText(l/1000+"s");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            btnAreaCode.setText("重获");
            btnAreaCode.setBackgroundResource(R.drawable.shape_login);
            //设置可点击
            btnAreaCode.setClickable(true);
        }
    }


    //注销EventHandler
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    /**
     * 使用Handler来分发Message对象到主线程中，处理事件
     */
    @SuppressLint("HandlerLeak")
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event=msg.arg1;
            int result=msg.arg2;
            Object data=msg.obj;
            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                if(result == SMSSDK.RESULT_COMPLETE) {
                    boolean smart = (Boolean)data;
                    if(smart) {
                        Toast.makeText(getApplicationContext(),"该手机号已经注册过，请重新输入",
                                Toast.LENGTH_LONG).show();
                        etPhone.requestFocus();
                        return;
                    }
                }
            }
            if(result==SMSSDK.RESULT_COMPLETE)
            {

                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码输入正确",
                            Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                if(flag)
                {
                    btnAreaCode.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"验证码获取失败请重新获取", Toast.LENGTH_LONG).show();
                    etPhone.requestFocus();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"验证码输入错误", Toast.LENGTH_LONG).show();
                }
            }

        }

    };
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
        if (etPassWd.getText().length()>6 && etPhone.getText().length()>10 && etAreaCode.getText().length()>0) {
            btnModify.setBackgroundResource(R.drawable.shape_login);
            btnModify.setEnabled(true);
        } else {
            btnModify.setBackgroundResource(R.drawable.shape_login_no);
            btnModify.setEnabled(false);
        }
    }
}
