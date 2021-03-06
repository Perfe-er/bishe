package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.ActivityAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ActivityInfo;
import com.example.zwq.assistant.been.Activity;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class ClassActionActivity extends AppCompatActivity {
    RecyclerView actRecycle;
    SwipeRefreshLayout actRefresh;
    List<Activity> mActivities;
    ImageView ivAdd;
    ImageView ivReturn;
    ImageView ivSearch;
    ActivityAdapter mActivityAdapter;
    LinearLayoutManager mLinearLayoutManager;
    private int page;
    private int classID;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_class_action);
        ivAdd = findViewById(R.id.ivAdd);
        ivReturn = findViewById(R.id.ivReturn);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().getLoginUser().getStuType() == 0){
                    Toast.makeText(ClassActionActivity.this,"你没有权限发布通知",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(ClassActionActivity.this, ActionPubActivity.class);
                    startActivity(intent);
                }
            }
        });
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassActionActivity.this,ActionSearchActivity.class);
                intent.putExtra("classID",classID + "");
                startActivity(intent);
            }
        });
        actRecycle = findViewById(R.id.actRecycle);
        actRefresh = findViewById(R.id.actRefresh);
        actRefresh.setColorSchemeColors(R.color.colorPrimary);
        actRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
                Toast.makeText(ClassActionActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = getIntent();
        classID = Integer.parseInt(intent.getStringExtra("classID"));
        initList();
        onItemClick();
        onItemLongClick();
    }

    public void initList(){
        mActivities = new ArrayList<>();
        mActivityAdapter = new ActivityAdapter(R.layout.item_activity_list,mActivities);
        mLinearLayoutManager = new LinearLayoutManager(ClassActionActivity.this);
        actRecycle.setAdapter(mActivityAdapter);
        actRecycle.setLayoutManager(mLinearLayoutManager);
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
                            Toast.makeText(ClassActionActivity.this,"暂无活动",Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(ClassActionActivity.this,ActionInfoActivity.class);
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
                                    Toast.makeText(ClassActionActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(ClassActionActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                return false;
            }
        });
    }
}
