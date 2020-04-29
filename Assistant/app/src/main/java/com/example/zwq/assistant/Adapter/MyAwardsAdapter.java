package com.example.zwq.assistant.Adapter;

import android.content.Intent;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.Activity.BaseActivity;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.AwardSign;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyAwardsAdapter extends BaseQuickAdapter<AwardSign, BaseViewHolder> {
    public MyAwardsAdapter(int layoutResId, @Nullable List<AwardSign> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AwardSign item) {
        int userID = item.getAwardsPub().getReleaseID();
        Date start = new Date(item.getAwardsPub().getStartTime());
        Date end = new Date(item.getAwardsPub().getEndTime());
        Date sign = new Date(item.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String startTime = sdf.format(start);
        String endTime = sdf.format(end);
        String signDate = sdf.format(sign);
        int pass = item.getPass();
        if (pass == 0){
            helper.setBackgroundRes(R.id.conBack,R.drawable.shape_wait);
            helper.setText(R.id.tvPass,"未审核");
        }else if (pass == 1){
            helper.setBackgroundRes(R.id.conBack,R.drawable.shape_yes);
            helper.setText(R.id.tvPass,"通过");
        }else {
            helper.setBackgroundRes(R.id.conBack,R.drawable.shape_no);
            helper.setText(R.id.tvPass,"不通过");
        }
        helper.setText(R.id.tvTitle,item.getAwardsPub().getAwardsTitle())
                .setText(R.id.tvStartTime,startTime)
                .setText(R.id.tvEndTime,endTime)
                .setText(R.id.tvSignTime,signDate);
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getUserInfoById(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200){
                            helper.setText(R.id.tvName,userHttpResult.getData().getName());
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
