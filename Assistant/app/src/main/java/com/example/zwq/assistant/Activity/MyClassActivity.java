package com.example.zwq.assistant.Activity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.ClassmateAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ClassInfo;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class MyClassActivity extends BaseActivity {
    ImageView ivReturn;
    ImageView ivSeekClass;
    ImageView ivAssistantPhone;
    ImageView ivAssistantSex;
    TextView tvAssistantName;
    TextView tvAssistantPhone;
    ConstraintLayout conAssistant;
    ConstraintLayout conNoClass;
    ConstraintLayout conClassmate;
    private List<User> mUserList;
    RecyclerView mRecyclerView;
    ClassmateAdapter mClassmateAdapter;
    LinearLayoutManager mLinearLayoutManager;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_class);
        initView();
        initList();
        initItemClick();
        initAssistant();
    }


    public void initView(){
        mRecyclerView = findViewById(R.id.rvStudent);
        ivReturn = findViewById(R.id.ivReturn);
        ivSeekClass = findViewById(R.id.ivSeekClass);
        ivAssistantPhone = findViewById(R.id.ivAssistantPhone);
        ivAssistantSex = findViewById(R.id.ivAssistantSex);
        tvAssistantName = findViewById(R.id.tvAssistantName);
        tvAssistantPhone = findViewById(R.id.tvAssistantPhone);
        conAssistant = findViewById(R.id.conAssistant);
        conNoClass = findViewById(R.id.conNoClass);
        conClassmate = findViewById(R.id.conClassmate);
        conAssistant.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
        ivSeekClass.setOnClickListener(this);
        ivAssistantPhone.setOnClickListener(this);
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.ivSeekClass:
                seekClass();
                break;
            case R.id.ivAssistantPhone:
                String phone = tvAssistantPhone.getText().toString();
                callPhone(phone);
                break;
            case R.id.conAssistant:
                Intent intent = new Intent(this,OtherInfoActivity.class);
                intent.putExtra("userID",userID + "");
                startActivity(intent);
                break;

        }
    }

    public void callPhone(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        startActivity(intent);
    }

    public void initAssistant(){
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getAssistantByClassID(UserInfoManager.getInstance().getLoginUser().getClassID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() != null){
                            tvAssistantPhone.setText(userHttpResult.getData().getPhone());
                            tvAssistantName.setText(userHttpResult.getData().getName());
                            int assistantSex = userHttpResult.getData().getSex();
                            if (assistantSex == 1){
                                ivAssistantSex.setImageResource(R.drawable.sex_g);
                            }else {
                                ivAssistantSex.setImageResource(R.drawable.sex_m);
                            }
                            userID = userHttpResult.getData().getId();
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

    public void initList(){
        mUserList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mClassmateAdapter = new ClassmateAdapter(this,mUserList);
        mRecyclerView.setAdapter(mClassmateAdapter);
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getClassmate(UserInfoManager.getInstance().getLoginUser().getClassID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<User>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<User>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            conNoClass.setVisibility(View.GONE);
                            conAssistant.setVisibility(View.VISIBLE);
                            conClassmate.setVisibility(View.VISIBLE);
                            mUserList.clear();
                            mUserList.addAll(listHttpResult.getData());
                            mClassmateAdapter.notifyDataSetChanged();
                        }else {
                            conNoClass.setVisibility(View.VISIBLE);
                            conAssistant.setVisibility(View.GONE);
                            conClassmate.setVisibility(View.GONE);
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
                Intent intent = new Intent(MyClassActivity.this,OtherInfoActivity.class);
                intent.putExtra("userID",userID + "");
                startActivity(intent);
               Toast.makeText(MyClassActivity.this,"个人信息查询",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChatItemClick(ImageView ivChat) {
                Toast.makeText(MyClassActivity.this,"聊天",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPhoneItemClick(ImageView ivPhone,String phone) {
                callPhone(phone);
                Toast.makeText(MyClassActivity.this,"电话",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void seekClass(){
        final View view = getLayoutInflater().inflate(R.layout.dialog_add_class, null);
        final EditText etClassName = view.findViewById(R.id.etClassName);
        new AlertDialog.Builder(this).setTitle("加入班级")
                .setIcon(null)
                .setView(view)
                .setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String className = etClassName.getText().toString();
                        RetrofitManager.getInstance().createReq(ClassInfo.class)
                                .findClassByClassName(className)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<HttpResult<Class>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(HttpResult<Class> classHttpResult) {
                                        if (classHttpResult.getCode() == 200 && classHttpResult.getData() != null){
                                            int classID = classHttpResult.getData().getClassID();
                                            String className1 = classHttpResult.getData().getClassName();
                                            int founderID = classHttpResult.getData().getFounderID();
                                            Toast.makeText(MyClassActivity.this,"找到班级",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MyClassActivity.this,SeekClassActivity.class);
                                            intent.putExtra("classID",classID + "");
                                            intent.putExtra("className1",className1);
                                            intent.putExtra("founderID",founderID + "");
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(MyClassActivity.this,"班级名错误",Toast.LENGTH_SHORT).show();
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

                }).setNegativeButton("取消",null).show();


    }
}
