package com.example.zwq.assistant.Adapter;

import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.AwardSign;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AwardRecordAdapter extends BaseQuickAdapter<AwardSign, BaseViewHolder> {
    public AwardRecordAdapter(int layoutResId, @Nullable List<AwardSign> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AwardSign item) {
        int userID = item.getUid();
        int pass = item.getPass();
        if (pass == 0) {
            helper.setBackgroundRes(R.id.conPassBack,R.drawable.shape_wait);
//            helper.setImageResource(R.id.conPassBack, R.drawable.shape_wait);
            helper.setText(R.id.tvPass,"未审核");
        }else if (pass == 1){
            helper.setBackgroundRes(R.id.conPassBack, R.drawable.shape_yes);
            helper.setText(R.id.tvPass,"通过");
        }else {
            helper.setBackgroundRes(R.id.conPassBack, R.drawable.shape_no);
            helper.setText(R.id.tvPass,"不通过");
        }
        Date date = new Date(item.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String dateTime = sdf.format(date);
        helper.setText(R.id.tvTime,dateTime);
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
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() != null){
                            int sex = userHttpResult.getData().getSex();
                            if (sex == 2){
                                helper.setImageResource(R.id.ivSex,R.drawable.sex_m);
                            }else {
                                helper.setImageResource(R.id.ivSex,R.drawable.sex_g);
                            }
                            helper.setText(R.id.tvName,userHttpResult.getData().getName());
                            helper.setText(R.id.tvClassName,userHttpResult.getData().getClassName());
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
