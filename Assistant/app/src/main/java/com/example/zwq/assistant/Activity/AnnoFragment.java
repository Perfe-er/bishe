package com.example.zwq.assistant.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.AnnoListAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AnnoInfo;
import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class AnnoFragment extends BaseFragment {
    RecyclerView rvAnnoList;
    List<Anno> mAnnoList;
    AnnoListAdapter mAnnoListAdapter;
    LinearLayoutManager mLinearLayoutManager;
    private int page;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anno,container,false);
        initList(view);
        return view;
    }

    public void initList(View view){
        rvAnnoList = view.findViewById(R.id.rvAnnoList);
        mAnnoList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mAnnoListAdapter = new AnnoListAdapter(R.layout.item_anno_list,mAnnoList);
        rvAnnoList.setLayoutManager(mLinearLayoutManager);
        rvAnnoList.setAdapter(mAnnoListAdapter);

        RetrofitManager.getInstance().createReq(AnnoInfo.class)
                .listAnno(UserInfoManager.getInstance().getLoginUser().getClassID(),page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<Anno>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<Anno>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mAnnoList.clear();
                            mAnnoList.addAll(listHttpResult.getData());
                            mAnnoListAdapter.notifyDataSetChanged();
                            page = page+1;
                        }else {
                            Toast.makeText(getContext(),"暂无公告",Toast.LENGTH_SHORT).show();
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
