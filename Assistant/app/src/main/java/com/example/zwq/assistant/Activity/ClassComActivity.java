package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.ClassmateAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class ClassComActivity extends AppCompatActivity {
    ImageView ivReturn;
    private int classID;
    RecyclerView comRecycle;
    ClassmateAdapter mClassmateAdapter;
    LinearLayoutManager mLinearLayoutManager;
    private List<User> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_class_com);
        ivReturn = findViewById(R.id.ivReturn);
        comRecycle = findViewById(R.id.comRecycle);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initList();
        initItemClick();
    }

    public void initList(){
        mUserList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        comRecycle.setLayoutManager(mLinearLayoutManager);
        mClassmateAdapter = new ClassmateAdapter(this,mUserList);
        comRecycle.setAdapter(mClassmateAdapter);
        if (UserInfoManager.getInstance().getLoginUser().getStuType() == 2){
            Intent intent = getIntent();
            classID = Integer.parseInt(intent.getStringExtra("classID"));
        }else {
            classID = UserInfoManager.getInstance().getLoginUser().getClassID();
        }
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getClassCom(classID)
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
                            Toast.makeText(ClassComActivity.this,"没有任命班委",Toast.LENGTH_SHORT).show();
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

    private void initItemClick(){
        mClassmateAdapter.setOnItemClickListener(new ClassmateAdapter.OnItemClick() {
            @Override
            public void onAllItemClick(int position,int userID) {
                Intent intent = new Intent(ClassComActivity.this,OtherInfoActivity.class);
                intent.putExtra("userID",userID + "");
                startActivity(intent);
                Toast.makeText(ClassComActivity.this,"个人信息查询",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPhoneItemClick(ImageView ivPhone,String phone) {
                callPhone(phone);
                Toast.makeText(ClassComActivity.this,"电话",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void callPhone(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        startActivity(intent);
    }
}
