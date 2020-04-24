package com.example.zwq.assistant.Adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.Activity;
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

public class ActivityAdapter extends BaseQuickAdapter<Activity,BaseViewHolder> {
    public ActivityAdapter(int layoutResId, @Nullable List<Activity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Activity item) {
        Date date = new Date(item.getActDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String dateTime = sdf.format(date);
        helper.setText(R.id.tvTitle,item.getActTitle())
                .setText(R.id.tvContent,item.getActContent())
                .setText(R.id.tvDateTime,dateTime);
        int actFouID = item.getActFouID();
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getUserInfoById(actFouID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200 && userHttpResult != null){
                            String actFounder = userHttpResult.getData().getName();
                            helper.setText(R.id.tvRelease,actFounder);
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
