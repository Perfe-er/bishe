package com.example.zwq.assistant.Activity;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;
public class SeekClassActivity extends BaseActivity {
    ImageView ivReturn;
    TextView tvClassName;
    TextView tvFounder;
    Button btnAdd;
    private int classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_seek_class);
        initView();
        intentInfo();
    }

    public void initView(){
        ivReturn = findViewById(R.id.ivReturn);
        tvClassName = findViewById(R.id.tvClassName);
        tvFounder = findViewById(R.id.tvFounder);
        btnAdd = findViewById(R.id.btnAdd);
        ivReturn.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.btnAdd:
                String className = tvClassName.getText().toString();
                RetrofitManager.getInstance().createReq(UserInfo.class)
                        .editClass(UserInfoManager.getInstance().getUid(),className,classID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<HttpResult<User>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(HttpResult<User> userHttpResult) {
                                if (userHttpResult.getCode() == 200 ){
                                    Intent intent = new Intent(SeekClassActivity.this,MeFragment.class);
                                    intent.putExtra("classID",classID);
                                    startActivity(intent);
                                    Toast.makeText(SeekClassActivity.this,"加入成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(SeekClassActivity.this,"加入失败",Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                break;
        }

    }

    public void intentInfo(){
        Intent intent = getIntent();
        tvClassName.setText(intent.getStringExtra("className1"));
        classID = Integer.parseInt(intent.getStringExtra("classID"));
        int founderID = Integer.parseInt(intent.getStringExtra("founderID"));
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getUserInfoById(founderID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() != null){
                            tvFounder.setText(userHttpResult.getData().getName());
                        }
                        else {
                            Toast.makeText(SeekClassActivity.this,"获取创建人失败",Toast.LENGTH_SHORT).show();
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
