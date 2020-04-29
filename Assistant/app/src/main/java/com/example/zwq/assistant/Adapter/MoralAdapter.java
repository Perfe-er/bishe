package com.example.zwq.assistant.Adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Moral;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoralAdapter extends BaseQuickAdapter<Moral, BaseViewHolder> {
    public MoralAdapter(int layoutResId, @Nullable List<Moral> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Moral item) {
        String add = String.valueOf(item.getAdd());
        String fine = String.valueOf(item.getFine());
        Date date = new Date(item.getDateTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String dateTime = sdf.format(date);
        helper.setText(R.id.tvAdd,add)
                .setText(R.id.tvFine,fine)
                .setText(R.id.tvReason,item.getReason())
                .setText(R.id.tvTime,dateTime);
        int userID = item.getChangeP();
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
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() != null) {
                            helper.setText(R.id.tvChangeP, userHttpResult.getData().getName());
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
