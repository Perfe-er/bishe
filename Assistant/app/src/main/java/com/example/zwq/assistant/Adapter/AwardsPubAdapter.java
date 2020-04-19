package com.example.zwq.assistant.Adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.Awards;
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

public class AwardsPubAdapter extends BaseQuickAdapter<Awards, BaseViewHolder> {
    public AwardsPubAdapter(int layoutResId, @Nullable List<Awards> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Awards item) {
        Date starDateTime = new Date(item.getStartTime());
        Date endDateTime = new Date(item.getEndTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String starTime = sdf.format(starDateTime);
        String endTime = sdf.format(endDateTime);
        helper.setText(R.id.tvTitle,item.getAwardsTitle())
                .setText(R.id.tvContent,item.getAwardsTitle())
                .setText(R.id.tvStartTime,starTime)
                .setText(R.id.tvEndTime,endTime);
        int releaseID = item.getReleaseID();
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getUserInfoById(releaseID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() != null) {
                            helper.setText(R.id.tvRelease, userHttpResult.getData().getName());
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
