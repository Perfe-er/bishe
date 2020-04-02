package com.example.zwq.assistant.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.BanjiAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ClassInfo;
import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyClassFragment extends BaseFragment {

    private List<Class> classList;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    BanjiAdapter mBanjiAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_class,container,false);
        initList();
        mRecyclerView = view.findViewById(R.id.rcMyClass);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mBanjiAdapter = new BanjiAdapter(getContext(),classList);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mBanjiAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    private void initList() {
        classList = new ArrayList<>();
        RetrofitManager.getInstance().createReq(ClassInfo.class)
                .showClassByFounder(UserInfoManager.getInstance().getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Class>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Class>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            classList.clear();
                            classList.addAll(listHttpResult.getData());
                        }else {
                        Toast.makeText(getContext(),"你没有班级",Toast.LENGTH_SHORT).show();
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
