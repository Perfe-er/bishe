package com.example.zwq.assistant.Adapter;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zwq.assistant.Activity.AnnoFragment;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.UserInfo;
import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;
import com.example.zwq.assistant.manager.RetrofitManager;

import java.util.List;

import androidx.annotation.Nullable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AssistantAnnoListAdapter extends BaseQuickAdapter<Class, BaseViewHolder> {

    public AssistantAnnoListAdapter(int layoutResId, @Nullable List<Class> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Class item) {
        helper.setText(R.id.tvClassName,item.getClassName());
        RetrofitManager.getInstance().createReq(UserInfo.class)
                .getUserInfoById(item.getFounderID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<User> userHttpResult) {
                        if (userHttpResult.getCode() == 200) {
                            helper.setText(R.id.tvFounder,userHttpResult.getData().getName());
                        }else {
                            Toast.makeText(mContext,"创建人出错",Toast.LENGTH_SHORT).show();
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
        int classID = item.getClassID();
    }
}
