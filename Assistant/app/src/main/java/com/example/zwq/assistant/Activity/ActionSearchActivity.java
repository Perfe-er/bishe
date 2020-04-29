package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class ActionSearchActivity extends AppCompatActivity {
    ImageView ivReturn;
    EditText etKeyWords;
    TextView tvCancel;
    RecyclerView searchRecycle;
    ImageView ivSearch;
    List<Activity> mActivities;
    ActivityAdapter mActivityAdapter;
    LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_action_search);
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
        mActivities = new ArrayList<>();
        mActivityAdapter = new ActivityAdapter(R.layout.item_activity_list,mActivities);
        mLinearLayoutManager = new LinearLayoutManager(this);
        searchRecycle.setAdapter(mActivityAdapter);
        searchRecycle.setLayoutManager(mLinearLayoutManager);
        String keyWorlds = etKeyWords.getText().toString();
        RetrofitManager.getInstance().createReq(ActivityInfo.class)
                .searchActivity(keyWorlds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Activity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Activity>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mActivities.clear();
                            mActivities.addAll(listHttpResult.getData());
                            mActivityAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(ActionSearchActivity.this,ActionInfoActivity.class);
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
                                    Toast.makeText(ActionSearchActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(ActionSearchActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
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
