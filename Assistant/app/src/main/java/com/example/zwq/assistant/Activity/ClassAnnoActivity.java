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
import com.example.zwq.assistant.Adapter.AnnoListAdapter;
import com.example.zwq.assistant.Adapter.AssistantAnnoListAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AnnoInfo;
import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class ClassAnnoActivity extends AppCompatActivity {
    RecyclerView rvAnnoList;
    List<Anno> mAnnoList;
    ImageView ivAdd;
    AnnoListAdapter mAnnoListAdapter;
    AssistantAnnoListAdapter assistantClass;
    LinearLayoutManager mLinearLayoutManager;
    SwipeRefreshLayout annoRefresh;
    ImageView ivSearch;
    private int page;
    private int classID;
    ImageView ivReturn;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_class_anno);
        ivReturn = findViewById(R.id.ivReturn);
        Intent intent = getIntent();
        classID = Integer.parseInt(intent.getStringExtra("classID"));
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassAnnoActivity.this,AnnoSearchActivity.class);
                intent.putExtra("classID",classID + "");
                startActivity(intent);
            }
        });
        rvAnnoList = findViewById(R.id.rvAnnoList);
        annoRefresh = findViewById(R.id.annoRefresh);
        ivAdd = findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().getLoginUser().getStuType() == 0){
                    Toast.makeText(ClassAnnoActivity.this,"你没有权限发布通知",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(ClassAnnoActivity.this, AnnoPubActivity.class);
                    startActivity(intent);
                }
            }
        });
        final int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
        initList();
        onItemClick();
        onItemLongClick();

        annoRefresh.setColorSchemeColors(R.color.colorPrimary);
        annoRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
                Toast.makeText(ClassAnnoActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initList(){
        mAnnoList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(ClassAnnoActivity.this);
        mAnnoListAdapter = new AnnoListAdapter(R.layout.item_anno_list,mAnnoList);
        rvAnnoList.setLayoutManager(mLinearLayoutManager);
        rvAnnoList.setAdapter(mAnnoListAdapter);
        RetrofitManager.getInstance().createReq(AnnoInfo.class)
                .listAnno(classID,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Anno>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Anno>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mAnnoList.clear();
                            mAnnoList.addAll(listHttpResult.getData());
                            mAnnoListAdapter.notifyDataSetChanged();
                            annoRefresh.setRefreshing(false);
                        }else {
                            Toast.makeText(ClassAnnoActivity.this,"暂无公告",Toast.LENGTH_SHORT).show();
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
        mAnnoListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int annoID = mAnnoList.get(position).getAnnoID();
                TextView tvTitle = view.findViewById(R.id.tvTitle);
                TextView tvContent = view.findViewById(R.id.tvContent);
                TextView tvRelease = view.findViewById(R.id.tvRelease);
                TextView tvDateTime = view.findViewById(R.id.tvDateTime);
                String title = tvTitle.getText().toString();
                String content = tvContent.getText().toString();
                String release = tvRelease.getText().toString();
                String dateTime = tvDateTime.getText().toString();
                Intent intent = new Intent(ClassAnnoActivity.this,AnnoInfoActivity.class);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                intent.putExtra("release",release);
                intent.putExtra("dateTime",dateTime);
                intent.putExtra("annoID",annoID + "");
                startActivity(intent);
            }
        });

    }

    public void onItemLongClick(){
        mAnnoListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                final int annoID = mAnnoList.get(position).getAnnoID();
                TextView tvTitle = view.findViewById(R.id.tvTitle);
                new AlertDialog.Builder(ClassAnnoActivity.this).setTitle("是否删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RetrofitManager.getInstance().createReq(AnnoInfo.class)
                                        .deleteAnno(annoID)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<HttpResult<Anno>>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onNext(HttpResult<Anno> annoHttpResult) {
                                                if (annoHttpResult.getCode() == 200 ){
                                                    mAnnoList.remove(position);
                                                    mAnnoListAdapter.notifyDataSetChanged();
                                                    Toast.makeText(ClassAnnoActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(ClassAnnoActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
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
                        }).setNegativeButton("取消", null).show();
                return false;
            }
        });
    }
}
