package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.ActivitySignAdatper;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ActivityInfo;
import com.example.zwq.assistant.been.ActSign;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class ActionSignActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    RecyclerView signRecycle;
    SwipeRefreshLayout signRefresh;
    ImageView ivReturn;
    ActivitySignAdatper mActivitySignAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<ActSign> mActSigns;
    TextView tvAdd;
    TextView tvFine;
    RadioGroup rgSelect;
    RadioButton rbAll;
    RadioButton rbNever;
    List<String> students;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_action_sign);
        initView();
        initList();
    }

    public void initView(){
        signRecycle = findViewById(R.id.signRecycle);
        signRefresh = findViewById(R.id.signRefresh);
        ivReturn = findViewById(R.id.ivReturn);
        tvAdd = findViewById(R.id.tvAdd);
        tvFine = findViewById(R.id.tvFine);
        rgSelect = findViewById(R.id.rgSelect);
        rbAll = findViewById(R.id.rbAll);
        rbNever = findViewById(R.id.rbNever);
        tvAdd.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
        tvFine.setOnClickListener(this);
        signRefresh.setOnClickListener(this);
        rbAll.setOnCheckedChangeListener(this);
        rbNever.setOnCheckedChangeListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.tvAdd:
                break;
            case R.id.tvFine:
                break;
            case R.id.ivReturn:
                finish();
                break;
            case R.id.signRefresh:
                initList();
                Toast.makeText(ActionSignActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void initList(){
        mActSigns = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mActivitySignAdapter = new ActivitySignAdatper(R.layout.item_sign_action,mActSigns);
        signRecycle.setLayoutManager(mLinearLayoutManager);
        signRecycle.setAdapter(mActivitySignAdapter);
        int classID = UserInfoManager.getInstance().getLoginUser().getClassID();
        Intent intent = getIntent();
        int actID = Integer.parseInt(intent.getStringExtra("actID"));
        int stuType = UserInfoManager.getInstance().getLoginUser().getStuType();
        RetrofitManager.getInstance().createReq(ActivityInfo.class)
                .signList(classID,actID,stuType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<ActSign>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<ActSign>> listHttpResult) {
                        if (listHttpResult.getCode() == 200){
                            mActSigns.clear();
                            mActSigns.addAll(listHttpResult.getData());
                            mActivitySignAdapter.notifyDataSetChanged();
                            signRefresh.setRefreshing(false);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (rbAll.isChecked()){
            mActivitySignAdapter.setAll();
        }else if (rbNever.isChecked()){
            mActivitySignAdapter.never();
        }
    }
}
