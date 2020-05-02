package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.AwardsPubAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AwardsInfo;
import com.example.zwq.assistant.been.Awards;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class ClassAwardsActivity extends AppCompatActivity {
    RecyclerView fillRecycle;
    SwipeRefreshLayout fillRefresh;
    LinearLayoutManager mLinearLayoutManager;
    AwardsPubAdapter mAwardsPubAdapter;
    List<Awards> mAwardsList;
    ImageView ivAdd;
    ImageView ivSearch;
    private int page;
    private int classID;
    ImageView ivReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_class_awards);
        ivReturn = findViewById(R.id.ivReturn);
        Intent intent = getIntent();
        classID = Integer.parseInt(intent.getStringExtra("classID"));
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivAdd = findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().getLoginUser().getStuType() == 0){
                    Toast.makeText(ClassAwardsActivity.this,"你没有权限发布通知",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(ClassAwardsActivity.this,AwardsPubActivity.class);
                    startActivity(intent);
                }
            }
        });
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassAwardsActivity.this,AwardSearchAcitivty.class);
                intent.putExtra("classID",classID + "");
                startActivity(intent);
            }
        });
        fillRecycle = findViewById(R.id.fillRecycle);
        fillRefresh = findViewById(R.id.fillRefresh);
        fillRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
            }
        });
        initList();
        onItemClick();
        onItemLongClick();
    }
    public void initList(){
        mAwardsList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(ClassAwardsActivity.this);
        mAwardsPubAdapter = new AwardsPubAdapter(R.layout.item_fill_awards,mAwardsList);
        fillRecycle.setAdapter(mAwardsPubAdapter);
        fillRecycle.setLayoutManager(mLinearLayoutManager);
        RetrofitManager.getInstance().createReq(AwardsInfo.class)
                .getListAwards(classID,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Awards>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Awards>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mAwardsList.clear();
                            mAwardsList.addAll(listHttpResult.getData());
                            fillRefresh.setRefreshing(false);
                            mAwardsPubAdapter.notifyDataSetChanged();
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
        mAwardsPubAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int awardID = mAwardsList.get(position).getAwardsID();
                TextView tvRelease = view.findViewById(R.id.tvRelease);
                String release = tvRelease.getText().toString();
                Intent intent = new Intent(ClassAwardsActivity.this,AwardsInfoActivity.class);
                intent.putExtra("awardID",awardID + "");
                intent.putExtra("release",release);
                startActivity(intent);
            }
        });
    }

    public void onItemLongClick(){
        mAwardsPubAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                final int awardID = mAwardsList.get(position).getAwardsID();
                String title = mAwardsList.get(position).getAwardsTitle();
                new AlertDialog.Builder(ClassAwardsActivity.this).setTitle("是否删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RetrofitManager.getInstance().createReq(AwardsInfo.class)
                                        .deleteAwards(awardID)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<HttpResult<Awards>>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onNext(HttpResult<Awards> awardsHttpResult) {
                                                if (awardsHttpResult.getCode() == 200){
                                                    mAwardsList.remove(position);
                                                    mAwardsPubAdapter.notifyDataSetChanged();
                                                    Toast.makeText(ClassAwardsActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
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
                        }).setNegativeButton("取消",null).show();
                return false;
            }
        });
    }
}
