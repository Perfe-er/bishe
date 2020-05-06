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

public class AnnoFragment extends BaseFragment {
    RecyclerView rvAnnoList;
    List<Anno> mAnnoList;
    ImageView ivAdd;
    ImageView ivSearch;
    AnnoListAdapter mAnnoListAdapter;
    LinearLayoutManager mLinearLayoutManager;
    SwipeRefreshLayout annoRefresh;
    private int page;

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anno,container,false);
        rvAnnoList = view.findViewById(R.id.rvAnnoList);
        annoRefresh = view.findViewById(R.id.annoRefresh);
        ivAdd = view.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoManager.getInstance().getLoginUser().getStuType() == 0){
                    Toast.makeText(getContext(),"你没有权限发布通知",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getContext(), AnnoPubActivity.class);
                    startActivity(intent);
                }
            }
        });
        ivSearch = view.findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AnnoSearchActivity.class);
                startActivity(intent);
            }
        });
        final int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
        if (userType == 2){
            initListById();
        }else {
            initList();
        }
        if (userType == 0){
            onItemClick();
        }else {
            onItemClick();
            onItemLongClick();
        }

        annoRefresh.setColorSchemeColors(R.color.colorPrimary);
        annoRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (userType == 2){
                    initListById();
                }else {
                    initList();
                }
                Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    public void initListById(){
        mAnnoList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mAnnoListAdapter = new AnnoListAdapter(R.layout.item_anno_list,mAnnoList);
        rvAnnoList.setLayoutManager(mLinearLayoutManager);
        rvAnnoList.setAdapter(mAnnoListAdapter);
        int uid = UserInfoManager.getInstance().getUid();
        RetrofitManager.getInstance().createReq(AnnoInfo.class)
                .listAnnoByAssistant(uid)
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
                            Toast.makeText(getContext(),"暂无公告",Toast.LENGTH_SHORT).show();
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
        mAnnoList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mAnnoListAdapter = new AnnoListAdapter(R.layout.item_anno_list,mAnnoList);
        rvAnnoList.setLayoutManager(mLinearLayoutManager);
        rvAnnoList.setAdapter(mAnnoListAdapter);
        int classID = UserInfoManager.getInstance().getLoginUser().getClassID();
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
                            Toast.makeText(getContext(),"暂无公告",Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(getContext(),AnnoInfoActivity.class);
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
                new AlertDialog.Builder(getContext()).setTitle("是否删除")
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
                        }).setNegativeButton("取消", null).show();
                return false;
            }
        });
    }
}
