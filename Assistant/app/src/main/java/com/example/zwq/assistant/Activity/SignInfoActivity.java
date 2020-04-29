package com.example.zwq.assistant.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.SignInfoAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.MoralInfo;
import com.example.zwq.assistant.Service.SignInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Moral;
import com.example.zwq.assistant.been.Sign;
import com.example.zwq.assistant.been.SignRecord;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

public class SignInfoActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    ImageView ivReturn;
    RadioGroup rgSign;
    RadioButton rbYes;
    RadioButton rbNo;
    RecyclerView recordRecycle;
    SwipeRefreshLayout recordRefresh;
    SignInfoAdapter mSignInfoAdapter;
    LinearLayoutManager mLinearLayoutManager;
    List<SignRecord> mSignRecords;
    TextView tvAdd;
    TextView tvFine;
    TextView tvStop;
    List<String> students;
    private int signID;
    private int signType;

    
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_info);
        initView();
        Intent intent = getIntent();
        signID = Integer.parseInt(intent.getStringExtra("signID"));
        signType = Integer.parseInt(intent.getStringExtra("signType"));
        if (signType == 1) {
            tvStop.setTextColor(R.color.burlywood);
            tvStop.setEnabled(false);
        }
        rgSign.check(R.id.rbYes);
    }

    public void initView(){
        recordRecycle = findViewById(R.id.recordRecycle);
        ivReturn = findViewById(R.id.ivReturn);
        ivReturn.setOnClickListener(this);
        rgSign = findViewById(R.id.rgSign);
        rbNo = findViewById(R.id.rbNo);
        rbYes = findViewById(R.id.rbYes);
        rbNo.setOnCheckedChangeListener(this);
        rbYes.setOnCheckedChangeListener(this);
        recordRefresh = findViewById(R.id.recordRefresh);
        recordRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (rbYes.isChecked()){
                    initList(1);
                }else if (rbNo.isChecked()){
                    initList(2);
                }
            }
        });
        tvAdd = findViewById(R.id.tvAdd);
        tvStop = findViewById(R.id.tvStop);
        tvAdd.setOnClickListener(this);
        tvStop.setOnClickListener(this);
        tvFine = findViewById(R.id.tvFine);
        tvFine.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        View v = getLayoutInflater().inflate(R.layout.dialog_moral,null);
        final EditText etNumber = v.findViewById(R.id.etNumber);
        final EditText etReason = v.findViewById(R.id.etReason);
        switch (view.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.tvStop:
                stopSign();
                break;
            case R.id.tvAdd:
                new AlertDialog.Builder(SignInfoActivity.this).setTitle("请输入数字")
                        .setView(v)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                double add = Double.parseDouble(etNumber.getText().toString());
                                String reason = etReason.getText().toString();
                                studentList(add,0,reason);
                            }
                        }).setNegativeButton("取消",null).show();
                break;
            case R.id.tvFine:
                new AlertDialog.Builder(SignInfoActivity.this).setTitle("请输入数字")
                        .setView(v)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                double fine = Double.parseDouble(etNumber.getText().toString());
                                String reason = etReason.getText().toString();
                                studentList(0,fine,reason);
                            }
                        }).setNegativeButton("取消",null).show();
                break;
        }
    }

    public void stopSign(){
        long endTime = System.currentTimeMillis();
        RetrofitManager.getInstance().createReq(SignInfo.class)
                .endSign(signID,endTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Sign>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Sign> signHttpResult) {
                        Toast.makeText(SignInfoActivity.this,signHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
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
        if (rbYes.isChecked()){
            initList(1);
        }else if (rbNo.isChecked()){
            initList(2);
        }
    }

    public void initList(int type){
        mSignRecords = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mSignInfoAdapter = new SignInfoAdapter(R.layout.item_sign_user,mSignRecords);
        recordRecycle.setLayoutManager(mLinearLayoutManager);
        recordRecycle.setAdapter(mSignInfoAdapter);
        RetrofitManager.getInstance().createReq(SignInfo.class)
                .getSignedUser(signID,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<List<SignRecord>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<List<SignRecord>> listHttpResult) {
                        if (listHttpResult.getCode() == 200 && listHttpResult.getData() != null){
                            mSignRecords.clear();
                            mSignRecords.addAll(listHttpResult.getData());
                            mSignInfoAdapter.notifyDataSetChanged();
                            recordRefresh.setRefreshing(false);
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void studentList(double add,double fine,String reason){
        students = new ArrayList<>();
        students = mSignInfoAdapter.getUsers();
        String stuID = String.join(",",students);
        int changeP = UserInfoManager.getInstance().getUid();
        long date = System.currentTimeMillis();
        RetrofitManager.getInstance().createReq(MoralInfo.class)
                .createMoral(stuID,changeP,reason,fine,add,date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Moral>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Moral> moralHttpResult) {
                        if (moralHttpResult.getCode() == 200){
                            Toast.makeText(SignInfoActivity.this,"修改德育成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SignInfoActivity.this,"修改德育失败",Toast.LENGTH_SHORT).show();
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
