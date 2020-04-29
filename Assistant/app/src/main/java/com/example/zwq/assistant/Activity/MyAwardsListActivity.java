package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.MyAwardsAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AwardsInfo;
import com.example.zwq.assistant.been.AwardSign;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class MyAwardsListActivity extends AppCompatActivity {
    RecyclerView mineRecycle;
    SwipeRefreshLayout mineRefresh;
    ImageView ivReturn;
    MyAwardsAdapter mMyAwardsAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<AwardSign> mAwardSigns;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_awards_list);
        ivReturn = findViewById(R.id.ivReturn);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mineRecycle = findViewById(R.id.mineRecycle);
        mineRefresh = findViewById(R.id.mineRefresh);
        mineRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        initList();
        onItemClick();
    }

    public void initList(){
        mAwardSigns = new ArrayList<>();
        mMyAwardsAdapter = new MyAwardsAdapter(R.layout.item_my_awards,mAwardSigns);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mineRecycle.setLayoutManager(mLinearLayoutManager);
        mineRecycle.setAdapter(mMyAwardsAdapter);
        int uid = UserInfoManager.getInstance().getUid();
        RetrofitManager.getInstance().createReq(AwardsInfo.class)
                .listMySignById(uid,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<AwardSign>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<AwardSign>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mAwardSigns.clear();
                            mAwardSigns.addAll(listHttpResult.getData());
                            mMyAwardsAdapter.notifyDataSetChanged();
                            mineRefresh.setRefreshing(false);
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
        mMyAwardsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int awardSignID = mAwardSigns.get(position).getAwardSignID();
                int userID = mAwardSigns.get(position).getUid();
                int pass = mAwardSigns.get(position).getPass();
                Intent intent = new Intent(MyAwardsListActivity.this,AwardSignInfoActivity.class);
                intent.putExtra("awardSignID",awardSignID +"");
                intent.putExtra("userID",userID +"");
                intent.putExtra("pass",pass +"");
                startActivity(intent);
            }
        });
    }

}
