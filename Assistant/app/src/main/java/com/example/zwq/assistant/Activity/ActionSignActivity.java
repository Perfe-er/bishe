package com.example.zwq.assistant.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zwq.assistant.Adapter.ActivitySignAdatper;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ActivityInfo;
import com.example.zwq.assistant.Service.MoralInfo;
import com.example.zwq.assistant.been.ActSign;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Moral;
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
    TextView tvNumber;
    RadioGroup rgSelect;
    RadioButton rbAll;
    RadioButton rbNever;
    List<String> students;
    ConstraintLayout conChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_action_sign);
        conChange = findViewById(R.id.conChange);
        if (UserInfoManager.getInstance().getLoginUser().getStuType() == 0){
            conChange.setVisibility(View.GONE);
        }else {
            conChange.setVisibility(View.VISIBLE);
        }
        initView();
        initList();
        onItemLongClick();
    }

    public void initView(){
        signRecycle = findViewById(R.id.signRecycle);
        signRefresh = findViewById(R.id.signRefresh);
        ivReturn = findViewById(R.id.ivReturn);
        tvAdd = findViewById(R.id.tvAdd);
        tvFine = findViewById(R.id.tvFine);
        tvNumber = findViewById(R.id.tvNumber);
        rgSelect = findViewById(R.id.rgSelect);
        rbAll = findViewById(R.id.rbAll);
        rbNever = findViewById(R.id.rbNever);
        tvAdd.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
        tvFine.setOnClickListener(this);
        signRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
                onItemLongClick();
                Toast.makeText(ActionSignActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
        rbAll.setOnCheckedChangeListener(this);
        rbNever.setOnCheckedChangeListener(this);
    }

    public void onClick(View view){
        View v = getLayoutInflater().inflate(R.layout.dialog_moral,null);
        final EditText etNumber = v.findViewById(R.id.etNumber);
        final EditText etReason = v.findViewById(R.id.etReason);
        switch (view.getId()){
            case R.id.tvAdd:
                new AlertDialog.Builder(ActionSignActivity.this).setTitle("请输入数字")
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
                new AlertDialog.Builder(ActionSignActivity.this).setTitle("请输入数字")
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
            case R.id.ivReturn:
                finish();
                break;
        }
    }

    public void onItemLongClick(){
        mActivitySignAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                int stuID = mActSigns.get(position).getStuID();
                int actSignID = mActSigns.get(position).getActSignID();
                if (stuID == UserInfoManager.getInstance().getUid()) {
                    new android.app.AlertDialog.Builder(ActionSignActivity.this).setTitle("是否取消报名")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RetrofitManager.getInstance().createReq(ActivityInfo.class)
                                            .deleteSign(actSignID)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<HttpResult<ActSign>>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onNext(HttpResult<ActSign> actSignHttpResult) {
                                                    if (actSignHttpResult.getCode() == 200){
                                                        Toast.makeText(ActionSignActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(ActionSignActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
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
                            }).setNegativeButton("取消",null).show();
                }else {
                    Toast.makeText(ActionSignActivity.this,"不是本人无法取消报名",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
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
                            String number = String.valueOf(mActSigns.size());
                            tvNumber.setText(number);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void studentList(double add,double fine,String reason){
        students = new ArrayList<>();
        students = mActivitySignAdapter.getStudents();
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
                            Toast.makeText(ActionSignActivity.this,"修改德育成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ActionSignActivity.this,"修改德育失败",Toast.LENGTH_SHORT).show();
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
//            for (int i = 0 ; i < mActivitySignAdapter.getData().size();i++){
//                if (mActivitySignAdapter.getData().get(i)
//            }

//            mActivitySignAdapter.setAll();
        }else if (rbNever.isChecked()){
//            mActivitySignAdapter.never();
        }
    }
}
