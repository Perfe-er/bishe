package com.example.zwq.assistant.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.AnnoPubClassAdapter;
import com.example.zwq.assistant.BuildConfig;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AnnoInfo;
import com.example.zwq.assistant.Service.AwardsInfo;
import com.example.zwq.assistant.Service.ClassInfo;
import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.Awards;
import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.CosManager;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;
import com.tencent.cos.xml.listener.CosXmlProgressListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AwardsPubActivity extends BaseActivity {
    EditText etTitle;
    EditText etContent;
    TextView tvPubAwards;
    TextView tvStartTime;
    TextView tvEndTime;
    TextView tvModifyFill;
    TextView tvPath;
    ImageView ivReturn;
    RecyclerView rvSelectClass;
    AnnoPubClassAdapter adapter;
    LinearLayoutManager mLinearLayoutManager;
    List<Class> mClassList;
    private Calendar cal;
    private List<String> pubClass;
    private int year,month,day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_awards_pub);
        initView();
        getDate();
        setClassList();
    }

    public void initView(){
        rvSelectClass = findViewById(R.id.rvSelectClass);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        tvPubAwards = findViewById(R.id.tvPubAwards);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvPath = findViewById(R.id.tvPath);
        tvModifyFill = findViewById(R.id.tvModifuFill);
        ivReturn = findViewById(R.id.ivReturn);
        tvPubAwards.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
        tvModifyFill.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View view){
        DatePickerDialog.OnDateSetListener listener;
        DatePickerDialog dialog;
        switch (view.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.tvPubAwards:
                pubAwards();
                break;
            case R.id.tvModifuFill:
                openSystemFile();
                break;
            case R.id.tvStartTime:
                listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        tvStartTime.setText(year+"年"+ (++month) +"月"+day + "日");      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                    }
                };
                dialog = new DatePickerDialog(this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.show();
                break;
            case R.id.tvEndTime:
                listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        tvEndTime.setText(year+"年"+ (++month) +"月"+day + "日");      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                    }
                };
                dialog = new DatePickerDialog(this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.show();
                break;
        }
    }

    public void openSystemFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        requestAllPower();
        // 所有类型
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择文件"), 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (null != uri) {
                String path = ContentUriUtil.getPath(this, uri);
                if (path != null) {
                    tvPath.setText(path);
                }else {
                    Toast.makeText(AwardsPubActivity.this,"文件不合法",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //权限动态申请

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pubAwards(){
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Date startTime1 = null;
        try {
            startTime1 = sdf.parse(tvStartTime.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endTime2 = null;
        try {
            endTime2 = sdf.parse(tvEndTime.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startTime = startTime1.getTime();
        long endTime = endTime2.getTime();
        pubClass = adapter.getPubClass();
        int releaseID = UserInfoManager.getInstance().getUid();
        String classID = String.join(",",pubClass);
        String path = tvPath.getText().toString();
        String uid = String.valueOf(UserInfoManager.getInstance().getUid());
        CosXmlProgressListener progressListener = null;
        CosManager.ICosXmlResultListener resultListener = new CosManager.ICosXmlResultListener() {
            @Override
            public void onSuccess(String url) {
                RetrofitManager.getInstance().createReq(AwardsInfo.class)
                        .pubAwards(releaseID,title,content,url,startTime,endTime,classID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<HttpResult<Awards>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(HttpResult<Awards> awardsHttpResult) {
                                Toast.makeText(AwardsPubActivity.this,awardsHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                if (awardsHttpResult.getCode() == 200){
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

            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(AwardsPubActivity.this,"文件上传失败",Toast.LENGTH_SHORT).show();
            }
        };
        CosManager.getInstance().uploadFile(path,uid,progressListener,resultListener);

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
    //获取当前日期
    private void getDate() {
        cal= Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
    }
}
