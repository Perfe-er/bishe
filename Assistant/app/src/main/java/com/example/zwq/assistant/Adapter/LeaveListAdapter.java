package com.example.zwq.assistant.Adapter;

import android.annotation.SuppressLint;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Leave;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;

import java.util.List;

import androidx.annotation.Nullable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LeaveListAdapter extends BaseQuickAdapter<Leave, BaseViewHolder> {
    private String phone;
    private String className;

    public LeaveListAdapter(int layoutResId, @Nullable List<Leave> data) {
        super(layoutResId, data);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void convert(BaseViewHolder helper, Leave item) {

        int stuID = item.getStuID();
        int sex = item.getSex();
        int ratify = item.getRatify();
        if (sex == 2){
            helper.setImageResource(R.id.ivSex,R.drawable.sex_m);
        }else {
            helper.setImageResource(R.id.ivSex,R.drawable.sex_g);
        }
        if (ratify == 0 ){
            helper.setBackgroundRes(R.id.conBack,R.drawable.shape_wait);
            helper.setText(R.id.tvRatify,"待审批");
        } else if (ratify == 1){
            helper.setBackgroundRes(R.id.conBack,R.drawable.shape_yes);
            helper.setText(R.id.tvRatify,"已批准");
        }else {
            helper.setBackgroundRes(R.id.conBack,R.drawable.shape_no);
            helper.setText(R.id.tvRatify,"不批准");
        }
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getUserInfoById(stuID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200){
                           phone = userHttpResult.getData().getPhone();
                           className = userHttpResult.getData().getClassName();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        helper.setText(R.id.tvName,item.getName())
                .setText(R.id.tvReason,item.getReason())
                .addOnClickListener(R.id.ivPhone);
    }

    public String getPhone() {
        return phone;
    }

    public String getClassName() {
        return className;
    }

}
