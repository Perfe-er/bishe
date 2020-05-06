package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.MySignAdapter;
import com.example.zwq.assistant.Adapter.SignListAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.SignInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Sign;
import com.example.zwq.assistant.been.SignRecord;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class SignActivity extends AppCompatActivity {
    TextView tvSign;
    ImageView ivReturn;
    SwipeRefreshLayout signRefresh;
    RecyclerView signRecycle;
    ConstraintLayout conSign;
    MySignAdapter mMySignAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<SignRecord> mSignRecords;
    private int userType;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign);
        userType = UserInfoManager.getInstance().getLoginUser().getStuType();
        conSign = findViewById(R.id.conSign);

        tvSign = findViewById(R.id.tvSign);
        tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignActivity.this,SignPubActivity.class);
                startActivity(intent);
            }
        });
        if (userType == 0){
            tvSign.setVisibility(View.GONE);
        }else if (userType == 2){
            conSign.setVisibility(View.GONE);
        }else {
            tvSign.setVisibility(View.VISIBLE);
            conSign.setVisibility(View.VISIBLE);
        }
        ivReturn = findViewById(R.id.ivReturn);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signRefresh = findViewById(R.id.signRefresh);
        signRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initUserList();
            }
        });
        signRecycle = findViewById(R.id.signRecycle);
        initUserList();
        onItemClick();
    }


    //用户接收到的签到
    public void initUserList(){
        mSignRecords = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMySignAdapter = new MySignAdapter(R.layout.item_my_sign_list,mSignRecords);
        signRecycle.setAdapter(mMySignAdapter);
        signRecycle.setLayoutManager(mLinearLayoutManager);
        RetrofitManager.getInstance().createReq(SignInfo.class)
                .getSiginOfUserRecev(UserInfoManager.getInstance().getUid(),page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<SignRecord>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<SignRecord>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mSignRecords.clear();
                            mSignRecords.addAll(listHttpResult.getData());
                            mMySignAdapter.notifyDataSetChanged();
                            signRefresh.setRefreshing(false);
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

    public void onItemClick(){
        mMySignAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int signID = mSignRecords.get(position).getSignId();
                Intent intent = new Intent(SignActivity.this,StuSignActivity.class);
                intent.putExtra("signID",signID + "");
                startActivity(intent);
            }
        });
    }
}
