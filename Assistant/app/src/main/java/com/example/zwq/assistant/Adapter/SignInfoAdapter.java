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

public class SignInfoAdapter extends BaseQuickAdapter<User, BaseViewHolder> {
    List<String> students = new ArrayList<>();

    public SignInfoAdapter(int layoutResId, @Nullable List<User> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, User item) {
        helper.setText(R.id.tvName,item.getName())
                .setText(R.id.tvClassName,item.getClassName());
        String stuID = String.valueOf(item.getId());
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
