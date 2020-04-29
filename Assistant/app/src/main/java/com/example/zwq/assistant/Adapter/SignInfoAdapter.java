package com.example.zwq.assistant.Adapter;

import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.SignRecord;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SignInfoAdapter extends BaseQuickAdapter<SignRecord, BaseViewHolder> {
    List<String> students = new ArrayList<>();

    public SignInfoAdapter(int layoutResId, @Nullable List<SignRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SignRecord item) {
        int userID;
        userID = item.getUid();
        Date date = new Date(item.getSignDate());
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
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() !=null){
                            helper.setText(R.id.tvName,userHttpResult.getData().getName())
                                    .setText(R.id.tvClassName,userHttpResult.getData().getClassName());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        String stuID = String.valueOf(userID);
        helper.setOnCheckedChangeListener(R.id.cbStudent, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    students.add(stuID);
                }else {
                    students.remove(stuID);
                }
            }
        });
    }

    public List<String> getUsers(){
        return students;
    }
}
