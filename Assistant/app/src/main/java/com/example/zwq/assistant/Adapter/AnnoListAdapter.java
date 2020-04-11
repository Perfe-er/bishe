package com.example.zwq.assistant.Adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.Anno;
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

public class AnnoListAdapter extends BaseQuickAdapter<Anno, BaseViewHolder> {

    public AnnoListAdapter(int layoutResId, @Nullable List<Anno> annoList) {
        super(layoutResId, annoList);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Anno item) {
        Date date = new Date(item.getReleDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String dateTime = sdf.format(date);
        int releaseID = item.getReleaseID();

        helper.setText(R.id.tvTitle,item.getAnnoTitle())
                .setText(R.id.tvContent,item.getContent())
                .setText(R.id.tvDateTime,dateTime);

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
                        if (userHttpResult.getCode() == 200 && userHttpResult != null){
                            String release = userHttpResult.getData().getName();
                            helper.setText(R.id.tvRelease,release);
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

        int position = helper.getLayoutPosition();
    }
}
