package com.example.zwq.assistant.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.BanjiAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ClassInfo;
import com.example.zwq.assistant.been.Class;
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

public class MyClassFragment extends BaseFragment {

    private List<Class> classList;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    BanjiAdapter mBanjiAdapter;
    SwipeRefreshLayout refresh;
    ImageView ivAdd;


    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_class,container,false);
        mRecyclerView = view.findViewById(R.id.rcMyClass);
        refresh = view.findViewById(R.id.refresh);
        ivAdd = view.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickClassName();
            }
        });
        initList();
        initLongClick();
        refresh.setColorSchemeColors(R.color.colorPrimary);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
                initLongClick();
                Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    private void initLongClick(){
        mRecyclerView.setLongClickable(true);
        mBanjiAdapter.setOnItemClickListener(new BanjiAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                int classID = classList.get(position).getClassID();
                new AlertDialog.Builder(getContext()).setTitle("是否删除班级")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RetrofitManager.getInstance().createReq(ClassInfo.class)
                                        .deleteClass(classID)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<HttpResult<Class>>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onNext(HttpResult<Class> classHttpResult) {
                                                if (classHttpResult.getCode() == 200) {
                                                    classList.remove(position);
                                                    mBanjiAdapter.notifyDataSetChanged();
                                                } else {
                                                    return;
                                                }
                                                Toast.makeText(getContext(), classHttpResult.getMsg(), Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText(getContext(), "网络出错", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            }
                        }).setNegativeButton("取消",null).show();

            }
        });

    }
    //请求class列表数据
    private void initList() {
        classList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBanjiAdapter = new BanjiAdapter(getContext(),classList);
        mRecyclerView.setAdapter(mBanjiAdapter);
        RetrofitManager.getInstance().createReq(ClassInfo.class)
                .showClassByFounder(UserInfoManager.getInstance().getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Class>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Class>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            classList.clear();
                            classList.addAll(listHttpResult.getData());
                            mBanjiAdapter.notifyDataSetChanged();
                            refresh.setRefreshing(false);
                        }else {
                            Toast.makeText(getContext(),"你没有班级",Toast.LENGTH_SHORT).show();
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


    //添加class
    public void OnClickClassName(){
        View v=getLayoutInflater().inflate(R.layout.dialog_add_class,null);
        final EditText etClassName=v.findViewById(R.id.etClassName);
        new AlertDialog.Builder(getContext()).setTitle("添加班级")
                .setIcon(null)
                .setView(v)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String className = etClassName.getText().toString();
                        RetrofitManager.getInstance().createReq(ClassInfo.class)
                                .createClass(className, UserInfoManager.getInstance().getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<HttpResult<Class>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(HttpResult<Class> classHttpResult) {
                                        if (classHttpResult.getCode() == 200){
                                            Toast.makeText(getContext(),classHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(getContext(),"创建失败",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(getContext(),"网络出错",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                }).setNegativeButton("取消",null).show();
    }


}
