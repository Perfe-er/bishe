package com.example.zwq.assistant.Activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.ClassmateAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

public class ClassManageActivity extends BaseActivity {
    ImageView ivReturn;
    TextView tvClassName;
    RecyclerView rvStudent;
    ClassmateAdapter mClassmateAdapter;
    LinearLayoutManager mLinearLayoutManager;
    private List<User> mUserList;
    private int classID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_class_manage);
        rvStudent = findViewById(R.id.rvStudent);
        ivReturn = findViewById(R.id.ivReturn);
        tvClassName = findViewById(R.id.tvClassName);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        classID = Integer.parseInt(intent.getStringExtra("classID"));
        tvClassName.setText(intent.getStringExtra("className"));
        initList();
        initItemClick();
    }

    public void initList(){
        mUserList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvStudent.setLayoutManager(mLinearLayoutManager);
        mClassmateAdapter = new ClassmateAdapter(this,mUserList);
        rvStudent.setAdapter(mClassmateAdapter);
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getClassmate(classID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<User>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<User>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mUserList.clear();
                            mUserList.addAll(listHttpResult.getData());
                            mClassmateAdapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(ClassManageActivity.this,"还没有学生加入",Toast.LENGTH_SHORT).show();
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

    public void callPhone(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        startActivity(intent);
    }

    private void initItemClick(){
        mClassmateAdapter.setOnItemClickListener(new ClassmateAdapter.OnItemClick() {
            @Override
            public void onAllItemClick(int position,int userID) {
                Intent intent = new Intent(ClassManageActivity.this,OtherInfoActivity.class);
                intent.putExtra("userID",userID + "");
                startActivity(intent);
                Toast.makeText(ClassManageActivity.this,"个人信息查询",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChatItemClick(ImageView ivChat) {
                Toast.makeText(ClassManageActivity.this,"聊天",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPhoneItemClick(ImageView ivPhone,String phone) {
                callPhone(phone);
                Toast.makeText(ClassManageActivity.this,"电话",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
