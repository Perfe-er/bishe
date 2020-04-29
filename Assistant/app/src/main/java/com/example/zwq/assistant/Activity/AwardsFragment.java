package com.example.zwq.assistant.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class AwardsFragment extends BaseFragment {

    RecyclerView fillRecycle;
    SwipeRefreshLayout fillRefresh;
    LinearLayoutManager mLinearLayoutManager;
    AwardsPubAdapter mAwardsPubAdapter;
    List<Awards> mAwardsList;
    ImageView ivAdd;
    ImageView ivSearch;
    ImageView ivRecord;
    private int page;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_awards,container,false);
        ivAdd = view.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().getLoginUser().getStuType() == 0){
                    Toast.makeText(getContext(),"你没有权限发布通知",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getContext(),AwardsPubActivity.class);
                    startActivity(intent);
                }
            }
        });
        fillRecycle = view.findViewById(R.id.fillRecycle);
        fillRefresh = view.findViewById(R.id.fillRefresh);
        fillRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
            }
        });
        ivRecord = view.findViewById(R.id.ivRecord);
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MyAwardsListActivity.class);
                startActivity(intent);
            }
        });
        ivSearch = view.findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AwardSearchAcitivty.class);
                startActivity(intent);
            }
        });
        initList();
        onItemClick();
        onItemLongClick();
        return view;
    }

    public void initList(){
        mAwardsList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mAwardsPubAdapter = new AwardsPubAdapter(R.layout.item_fill_awards,mAwardsList);
        fillRecycle.setAdapter(mAwardsPubAdapter);
        fillRecycle.setLayoutManager(mLinearLayoutManager);
        int classID = UserInfoManager.getInstance().getLoginUser().getClassID();
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
                Intent intent = new Intent(getContext(),AwardsInfoActivity.class);
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
                new AlertDialog.Builder(getContext()).setTitle("是否删除")
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
                                                    Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT).show();
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
