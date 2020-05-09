package com.example.zwq.assistant.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.ActivityAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ActivityInfo;
import com.example.zwq.assistant.Service.AnnoInfo;
import com.example.zwq.assistant.been.Activity;
import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActionFragment extends BaseFragment {
    RecyclerView actRecycle;
    SwipeRefreshLayout actRefresh;
    List<Activity> mActivities;
    ImageView ivAdd;
    ImageView ivSearch;
    ActivityAdapter mActivityAdapter;
    LinearLayoutManager  mLinearLayoutManager;
    private int page;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action,container,false);
        ivAdd = view.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().getLoginUser().getStuType() == 0){
                    Toast.makeText(getContext(),"你没有权限发布通知",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getContext(), ActionPubActivity.class);
                    startActivity(intent);
                }
            }
        });
        ivSearch = view.findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ActionSearchActivity.class);
                startActivity(intent);
            }
        });
        actRecycle = view.findViewById(R.id.actRecycle);
        actRefresh = view.findViewById(R.id.actRefresh);
        int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
        if (userType == 2){
            assistantList();
        }else {
            initList();
        }
        actRefresh.setColorSchemeColors(R.color.colorPrimary);
        actRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (userType == 2){
                    assistantList();
                    onItemClick();
                    onItemLongClick();
                }else {
                    initList();
                    onItemClick();
                    onItemLongClick();
                }
                Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
        if (userType == 0){
            onItemClick();
        }else {
            onItemClick();
            onItemLongClick();
        }

        return view;
    }

    public void assistantList(){
        mActivities = new ArrayList<>();
        mActivityAdapter = new ActivityAdapter(R.layout.item_activity_list,mActivities);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        actRecycle.setAdapter(mActivityAdapter);
        actRecycle.setLayoutManager(mLinearLayoutManager);
        int uid = UserInfoManager.getInstance().getUid();
        RetrofitManager.getInstance().createReq(ActivityInfo.class)
                .assistantList(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Activity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Activity>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null) {
                            mActivities.clear();
                            mActivities.addAll(listHttpResult.getData());
                            mActivityAdapter.notifyDataSetChanged();
                            actRefresh.setRefreshing(false);
                        }else {
                            Toast.makeText(getContext(),"暂无活动",Toast.LENGTH_SHORT).show();
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
        mActivities = new ArrayList<>();
        mActivityAdapter = new ActivityAdapter(R.layout.item_activity_list,mActivities);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        actRecycle.setAdapter(mActivityAdapter);
        actRecycle.setLayoutManager(mLinearLayoutManager);
        int classID = UserInfoManager.getInstance().getLoginUser().getClassID();
        RetrofitManager.getInstance().createReq(ActivityInfo.class)
                .listActivity(classID,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Activity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Activity>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null) {
                            mActivities.clear();
                            mActivities.addAll(listHttpResult.getData());
                            mActivityAdapter.notifyDataSetChanged();
                            actRefresh.setRefreshing(false);
                        }else {
                            Toast.makeText(getContext(),"暂无活动",Toast.LENGTH_SHORT).show();
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
        mActivityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int actID = mActivities.get(position).getActID();
                TextView tvTitle = view.findViewById(R.id.tvTitle);
                TextView tvContent = view.findViewById(R.id.tvContent);
                TextView tvRelease = view.findViewById(R.id.tvRelease);
                TextView tvDateTime = view.findViewById(R.id.tvDateTime);
                String title = tvTitle.getText().toString();
                String content = tvContent.getText().toString();
                String release = tvRelease.getText().toString();
                String dateTime = tvDateTime.getText().toString();
                Intent intent = new Intent(getContext(),ActionInfoActivity.class);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                intent.putExtra("release",release);
                intent.putExtra("dateTime",dateTime);
                intent.putExtra("actID",actID + "");
                startActivity(intent);
            }
        });
    }

    public void onItemLongClick(){
        mActivityAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                int actID = mActivities.get(position).getActID();
                new AlertDialog.Builder(getContext()).setTitle("是否删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RetrofitManager.getInstance().createReq(ActivityInfo.class)
                                        .deleteActivity(actID)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<HttpResult<Activity>>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onNext(HttpResult<Activity> activityHttpResult) {
                                                if (activityHttpResult.getCode() == 200 ){
                                                    mActivities.remove(position);
                                                    mActivityAdapter.notifyDataSetChanged();
                                                    Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(getContext(),"删除失败",Toast.LENGTH_SHORT).show();
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
