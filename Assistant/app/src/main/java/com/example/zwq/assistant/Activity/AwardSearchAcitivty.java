package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class AwardSearchAcitivty extends AppCompatActivity {
    LinearLayoutManager mLinearLayoutManager;
    AwardsPubAdapter mAwardsPubAdapter;
    List<Awards> mAwardsList;
    ImageView ivReturn;
    EditText etKeyWords;
    TextView tvCancel;
    RecyclerView searchRecycle;
    ImageView ivSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_award_search_acitivty);
        initView();
    }

    public void initView(){
        ivReturn = findViewById(R.id.ivReturn);
        etKeyWords = findViewById(R.id.etKeyWords);
        tvCancel = findViewById(R.id.tvCancel);
        ivSearch = findViewById(R.id.ivSearch);
        searchRecycle = findViewById(R.id.searchRecycle);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etKeyWords.getText().clear();
            }
        });
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList();
                onItemClick();
                onItemLongClick();
            }
        });
    }

    public void searchList(){
        mAwardsList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mAwardsPubAdapter = new AwardsPubAdapter(R.layout.item_fill_awards,mAwardsList);
        searchRecycle.setAdapter(mAwardsPubAdapter);
        searchRecycle.setLayoutManager(mLinearLayoutManager);
        String keyWorlds = etKeyWords.getText().toString();
        RetrofitManager.getInstance().createReq(AwardsInfo.class)
                .searchAwards(keyWorlds)
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
                Intent intent = new Intent(AwardSearchAcitivty.this,AwardsInfoActivity.class);
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
                new AlertDialog.Builder(AwardSearchAcitivty.this).setTitle("是否删除")
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
                                                    Toast.makeText(AwardSearchAcitivty.this,"删除成功",Toast.LENGTH_SHORT).show();
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
