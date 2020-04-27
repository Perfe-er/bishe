package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.LeaveListAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.LeaveInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Leave;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class LeaveRecordActivity extends BaseActivity {
    RecyclerView recycleLeave;
    LeaveListAdapter mLeaveListAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<Leave> mLeaves;
    SwipeRefreshLayout refreshLeave;
    ImageView ivReturn;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_leave_record);
        recycleLeave = findViewById(R.id.recycleLeave);
        refreshLeave = findViewById(R.id.refreshLeave);
        ivReturn = findViewById(R.id.ivReturn);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshLeave.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
            }
        });
        initList();
        onItemChildClick();
        onItemClick();
        onItemLongClick();
    }

    public void onItemLongClick(){
        mLeaveListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                int leaveID = mLeaves.get(position).getLeaveID();
                int stuID = mLeaves.get(position).getStuID();
                int uid = UserInfoManager.getInstance().getUid();
                if (stuID == uid){
                    new AlertDialog.Builder(LeaveRecordActivity.this).setTitle("是否删除")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RetrofitManager.getInstance().createReq(LeaveInfo.class)
                                            .delLeave(leaveID,uid)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<HttpResult<Leave>>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onNext(HttpResult<Leave> leaveHttpResult) {
                                                    Toast.makeText(LeaveRecordActivity.this,leaveHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                                    if (leaveHttpResult.getCode() == 200) {
                                                        mLeaves.remove(leaveID);
                                                        mLeaveListAdapter.notifyDataSetChanged();
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

                }else {
                    Toast.makeText(LeaveRecordActivity.this,"只有本人才能删除",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
    public void onItemClick(){
        mLeaveListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int leaveID = mLeaves.get(position).getLeaveID();
                String phone = mLeaveListAdapter.getPhone();
                String className = mLeaveListAdapter.getClassName();
                Intent intent = new Intent(LeaveRecordActivity.this,LeaveInfoActivity.class);
                intent.putExtra("leaveID",leaveID + "");
                intent.putExtra("className",className);
                intent.putExtra("phone",phone);
                startActivity(intent);
            }
        });
    }
    public void onItemChildClick(){
        mLeaveListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.tvStuID:
                        String phone = mLeaveListAdapter.getPhone();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + phone);
                        intent.setData(data);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    public void initList(){
        mLeaves = new ArrayList<>();
        mLeaveListAdapter = new LeaveListAdapter(R.layout.item_leave_record,mLeaves);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recycleLeave.setLayoutManager(mLinearLayoutManager);
        recycleLeave.setAdapter(mLeaveListAdapter);
        int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
        int classID = UserInfoManager.getInstance().getLoginUser().getClassID();
        int userID = UserInfoManager.getInstance().getUid();
        if (userType == 2){
            RetrofitManager.getInstance().createReq(LeaveInfo.class)
                    .listLeaveByRatifyID(userID,page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<HttpResult<List<Leave>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(HttpResult<List<Leave>> listHttpResult) {
                            if (listHttpResult.getCode() == 200) {
                                mLeaves.clear();
                                mLeaves.addAll(listHttpResult.getData());
                                mLeaveListAdapter.notifyDataSetChanged();
                                refreshLeave.setRefreshing(false);
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
        }else if (userType == 1){
            RetrofitManager.getInstance().createReq(LeaveInfo.class)
                    .listLeaveByClassID(classID,page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<HttpResult<List<Leave>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(HttpResult<List<Leave>> listHttpResult) {
                            if (listHttpResult.getCode() == 200) {
                                mLeaves.clear();
                                mLeaves.addAll(listHttpResult.getData());
                                mLeaveListAdapter.notifyDataSetChanged();
                                refreshLeave.setRefreshing(false);
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
        }else {
            RetrofitManager.getInstance().createReq(LeaveInfo.class)
                    .listlLeve(userID,page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<HttpResult<List<Leave>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(HttpResult<List<Leave>> listHttpResult) {
                            if (listHttpResult.getCode() == 200) {
                                mLeaves.clear();
                                mLeaves.addAll(listHttpResult.getData());
                                mLeaveListAdapter.notifyDataSetChanged();
                                refreshLeave.setRefreshing(false);
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
    }
}
