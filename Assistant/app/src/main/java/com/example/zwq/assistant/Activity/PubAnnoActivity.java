package com.example.zwq.assistant.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.AnnoPubClassAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AnnoInfo;
import com.example.zwq.assistant.Service.ClassInfo;
import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class PubAnnoActivity extends BaseActivity {
    EditText etTitle;
    EditText etContent;
    TextView tvPubAnno;
    ImageView ivReturn;
    RecyclerView rvSelectClass;
    AnnoPubClassAdapter adapter;
    LinearLayoutManager mLinearLayoutManager;
    List<Class> mClassList;
    private List<String> pubClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pub_anno);
        initView();
        setClassList();
    }

    public void initView(){
        rvSelectClass = findViewById(R.id.rvSelectClass);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        tvPubAnno = findViewById(R.id.tvPubAnno);
        ivReturn = findViewById(R.id.ivReturn);
        tvPubAnno.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.tvPubAnno:
                pubAnno();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pubAnno(){
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        int releaseID = UserInfoManager.getInstance().getUid();
        Long date = System.currentTimeMillis();
        pubClass = adapter.getPubClass();
        String classID = String.join(",",pubClass);
        RetrofitManager.getInstance().createReq(AnnoInfo.class)
                .pubAnno(title,content,releaseID,date,classID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Anno>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Anno> annoHttpResult) {
                        Toast.makeText(PubAnnoActivity.this,annoHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                        if (annoHttpResult.getCode() == 200){
                            finish();
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

    public void setClassList(){
        pubClass = new ArrayList<>();
        mClassList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        adapter = new AnnoPubClassAdapter(R.layout.item_anno_class,mClassList);
        rvSelectClass.setLayoutManager(mLinearLayoutManager);
        rvSelectClass.setAdapter(adapter);
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
                        if (listHttpResult.getCode() == 200 ){
                            mClassList.clear();
                            mClassList.addAll(listHttpResult.getData());
                            adapter.notifyDataSetChanged();
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
