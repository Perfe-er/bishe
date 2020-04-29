package com.example.zwq.assistant.Adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.ActSign;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.Nullable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivitySignAdatper extends BaseQuickAdapter<ActSign, BaseViewHolder> {
    private List<String> students = new ArrayList<>();

    public ActivitySignAdatper(int layoutResId, @Nullable List<ActSign> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActSign item) {
        Date date = new Date(item.getSignDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String dateTime = sdf.format(date);
        helper.setText(R.id.tvTime,dateTime);
        int stuID = item.getStuID();
        if (UserInfoManager.getInstance().getLoginUser().getStuType() == 0){
            helper.getView(R.id.cbSelect).setVisibility(View.GONE);
        }else {
            helper.getView(R.id.cbSelect).setVisibility(View.VISIBLE);
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
                        if (userHttpResult.getCode() == 200 && userHttpResult.getData() != null){
                            helper.setText(R.id.tvName,userHttpResult.getData().getName())
                                    .setText(R.id.tvStuID,userHttpResult.getData().getStuID())
                                    .setText(R.id.tvClass,userHttpResult.getData().getClassName());
                            int sex = userHttpResult.getData().getSex();
                            if (sex == 2){
                                helper.setImageResource(R.id.ivSex,R.drawable.sex_m);
                            }else {
                                helper.setImageResource(R.id.ivSex,R.drawable.sex_g);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        String userID = String.valueOf(stuID);
        helper.setOnCheckedChangeListener(R.id.cbSelect, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    students.add(userID);
                }else {
                    students.remove(userID);
                }
            }
        });
    }


    public List<String> getStudents(){
        return students;
    }
}
