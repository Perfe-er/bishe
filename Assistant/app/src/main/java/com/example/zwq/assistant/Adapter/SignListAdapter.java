package com.example.zwq.assistant.Adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Sign;
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

public class SignListAdapter extends BaseQuickAdapter<Sign, BaseViewHolder> {

    public SignListAdapter(int layoutResId, @Nullable List<Sign> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Sign item) {
        int originID = item.getOriginID();
        Date start = new Date(item.getIniDate());
        Date end = new Date(item.getEndDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String startTime = sdf.format(start);
        String endTime = sdf.format(end);
        int signType = item.getSignType();
        if (signType == 0){
            helper.setBackgroundRes(R.id.conBack,R.drawable.shape_yes);
        }else {
            helper.setBackgroundRes(R.id.conBack,R.drawable.shape_no);
        }
        helper.setText(R.id.tvReason,item.getSigncol())
                .setText(R.id.tvStartTime,startTime)
                .setText(R.id.tvEndTime,endTime);

        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getUserInfoById(originID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() !=null){
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
