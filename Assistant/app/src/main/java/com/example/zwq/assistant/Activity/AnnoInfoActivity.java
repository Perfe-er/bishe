package com.example.zwq.assistant.Activity;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AnnoInfo;
import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;


public class AnnoInfoActivity extends BaseActivity {
    ImageView ivReturn;
    TextView tvSave;
    TextView tvModify;
    TextView tvRelease;
    TextView tvTime;
    EditText etTitle;
    EditText etContent;
    private int annoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_anno_info);
        initView();
        intentAnno();
    }
    public void initView(){
        ivReturn = findViewById(R.id.ivReturn);
        tvSave = findViewById(R.id.tvSave);
        tvModify = findViewById(R.id.tvModify);
        tvRelease = findViewById(R.id.tvRelease);
        tvTime = findViewById(R.id.tvTime);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        ivReturn.setOnClickListener(this);
        tvModify.setOnClickListener(this);
        tvSave.setOnClickListener(this);
         int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
         if (userType == 0){
             tvModify.setVisibility(View.GONE);
             tvSave.setVisibility(View.GONE);
         }else {
             tvModify.setVisibility(View.VISIBLE);
             tvSave.setVisibility(View.VISIBLE);
         }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.tvSave:
                editAnno();
                finish();
                break;
            case R.id.tvModify:
                etTitle.setEnabled(true);
                etContent.setEnabled(true);
                tvModify.setTextColor(Color.parseColor("#ABABAB"));
                break;
            case R.id.ivReturn:
                finish();
                break;
        }
    }

    public void editAnno(){
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        int releaseID = UserInfoManager.getInstance().getUid();
        Long date = System.currentTimeMillis();
        RetrofitManager.getInstance().createReq(AnnoInfo.class)
                .modifyAnno(annoID,title,content,releaseID,date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Anno>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Anno> annoHttpResult) {
                        Toast.makeText(AnnoInfoActivity.this,annoHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void intentAnno(){
        Intent intent = getIntent();
        etTitle.setText(intent.getStringExtra("title"));
        etContent.setText(intent.getStringExtra("content"));
        tvRelease.setText(intent.getStringExtra("release"));
        tvTime.setText(intent.getStringExtra("dateTime"));
        annoID = Integer.valueOf(intent.getStringExtra("annoID"));
    }
}
