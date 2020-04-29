package com.example.zwq.assistant.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.AnnoPubClassAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ClassInfo;
import com.example.zwq.assistant.Service.SignInfo;
import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Sign;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignPubActivity extends BaseActivity {
    EditText etReason;
    Button btnPub;
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
        setContentView(R.layout.activity_sign_pub);
        initView();
        setClassList();
    }

    public void initView(){
        rvSelectClass = findViewById(R.id.rvSelectClass);
        etReason = findViewById(R.id.etReason);
        btnPub = findViewById(R.id.btnPub);
        ivReturn = findViewById(R.id.ivReturn);
        btnPub.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.btnPub:
                pubSign();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pubSign(){
        String reason = etReason.getText().toString();
        Long date = System.currentTimeMillis();
        int uid = UserInfoManager.getInstance().getUid();
        pubClass = adapter.getPubClass();
        String classID = String.join(",",pubClass);
        RetrofitManager.getInstance().createReq(SignInfo.class)
                .pubSign(uid,date,reason,classID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Sign>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Sign> signHttpResult) {
                        Toast.makeText(SignPubActivity.this,signHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                        if (signHttpResult.getCode() == 200 ){
                            finish();
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
