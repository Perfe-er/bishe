package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.MoralAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.MoralInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Moral;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class MyMoralRecordActivity extends AppCompatActivity {
    ImageView ivReturn;
    RecyclerView moralRecycle;
    SwipeRefreshLayout moralRefresh;
    MoralAdapter mMoralAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<Moral> mMorals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_moral_record);
        ivReturn = findViewById(R.id.ivReturn);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        moralRecycle = findViewById(R.id.moralRecycle);
        moralRefresh = findViewById(R.id.moralRefresh);
        moralRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
                Toast.makeText(MyMoralRecordActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
        initList();
    }

    public void initList(){
        mMorals = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMoralAdapter = new MoralAdapter(R.layout.item_moral_record,mMorals);
        moralRecycle.setAdapter(mMoralAdapter);
        moralRecycle.setLayoutManager(mLinearLayoutManager);
        RetrofitManager.getInstance().createReq(MoralInfo.class)
                .moralRecord(UserInfoManager.getInstance().getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Moral>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Moral>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null) {
                            mMorals.clear();
                            mMorals.addAll(listHttpResult.getData());
                            mMoralAdapter.notifyDataSetChanged();
                            moralRefresh.setRefreshing(false);
                        }else {
                            Toast.makeText(MyMoralRecordActivity.this,"没有记录",Toast.LENGTH_SHORT).show();
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
