package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.SignListAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.SignInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Sign;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class SignRecordActivity extends AppCompatActivity {
    ImageView ivReturn;
    SwipeRefreshLayout signRefresh;
    RecyclerView signRecycle;
    SignListAdapter mSignListAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<Sign> mSigns;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_record);
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
                initList();
                onItemClick();
            }
        });
        signRecycle = findViewById(R.id.signRecycle);
        initList();
        onItemClick();
    }

    public void initList(){
        mSigns = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mSignListAdapter = new SignListAdapter(R.layout.item_sign_list,mSigns);
        signRecycle.setAdapter(mSignListAdapter);
        signRecycle.setLayoutManager(mLinearLayoutManager);
        RetrofitManager.getInstance().createReq(SignInfo.class)
                .getSiginOfUserPub(UserInfoManager.getInstance().getUid(),page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Sign>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Sign>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mSigns.clear();
                            mSigns.addAll(listHttpResult.getData());
                            mSignListAdapter.notifyDataSetChanged();
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
        mSignListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int signID = mSigns.get(position).getSignID();
                int signType = mSigns.get(position).getSignType();
                Intent intent = new Intent(SignRecordActivity.this,SignInfoActivity.class);
                intent.putExtra("signID", signID + "");
                intent.putExtra("signType",signType + "");
                startActivity(intent);
            }
        });
    }
}
