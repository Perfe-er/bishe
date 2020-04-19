package com.example.zwq.assistant.Activity;
import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AwardsInfo;
import com.example.zwq.assistant.been.Awards;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.CosManager;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AwardsInfoActivity extends BaseActivity {
    EditText etTitle;
    EditText etContent;
    TextView tvStarTime;
    TextView tvEndTime;
    TextView tvSave;
    TextView tvRelease;
    TextView tvModify;
    TextView tvModifyFill;
    TextView tvPath;
    TextView tvDownLoad;
    ImageView ivReturn;
    private Calendar cal;
    private int year,month,day;
    private int awardID;
    private int releaseID;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_awards_info);
        getDate();
        initView();
        intentInfo();

    }

    public void initView(){
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        tvStarTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvSave = findViewById(R.id.tvSave);
        tvRelease = findViewById(R.id.tvRelease);
        tvModify = findViewById(R.id.tvModify);
        tvModifyFill = findViewById(R.id.tvModifuFill);
        tvDownLoad = findViewById(R.id.tvDownLoad);
        tvPath = findViewById(R.id.tvPath);
        tvModify = findViewById(R.id.tvModify);
        ivReturn = findViewById(R.id.ivReturn);
        ivReturn.setOnClickListener(this);
        tvStarTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        tvModify.setOnClickListener(this);
        tvModifyFill.setOnClickListener(this);
        tvDownLoad.setOnClickListener(this);

    }


    public void onClick(View view){
        DatePickerDialog.OnDateSetListener listener;
        DatePickerDialog dialog;
        switch (view.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.tvStartTime:
                listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        tvStarTime.setText(year+"年"+ (++month) +"月"+day + "日");      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                    }
                };
                dialog = new DatePickerDialog(this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.show();
                break;
            case R.id.tvEndTime:
                listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        tvEndTime.setText(year +"年"+(++month)+"月"+day + "日");
                    }
                };
                dialog = new DatePickerDialog(this, 0,listener,year,month,day);
                dialog.show();
                break;
            case R.id.tvSave:
                saveAwards();
                break;
            case R.id.tvModify:
                etTitle.setEnabled(true);
                etContent.setEnabled(true);
                tvModify.setTextColor(Color.parseColor("#ABABAB"));
                tvStarTime.setEnabled(true);
                tvEndTime.setEnabled(true);
                tvModifyFill.setEnabled(true);
                tvDownLoad.setVisibility(View.GONE);
                break;
            case R.id.tvDownLoad:
                downLoad();
                break;
            case R.id.tvModifuFill:
                openSystemFile();
                break;
        }


    }

    public void downLoad(){
        CosXmlProgressListener progressListener = null;
        CosXmlResultListener resultListener =new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Toast.makeText(AwardsInfoActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Toast.makeText(AwardsInfoActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
            }
        };
        CosManager.getInstance().downLoad(url,progressListener,resultListener);
    }

    public void openSystemFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
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
                    Toast.makeText(AwardsInfoActivity.this,"文件不合法",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveAwards(){
        String title = etTitle.getText().toString();
        String content = etTitle.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Date startTime1 = null;
        try {
            startTime1 = sdf.parse(tvStarTime.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endTime1 = null;
        try {
            endTime1 = sdf.parse(tvEndTime.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startTime = startTime1.getTime();
        long endTime = endTime1.getTime();
        String path = tvPath.getText().toString();
        int id = UserInfoManager.getInstance().getUid();

        CosXmlProgressListener progressListener = null;
        CosManager.ICosXmlResultListener resultListener =new CosManager.ICosXmlResultListener() {
            @Override
            public void onSuccess(String url) {
                RetrofitManager.getInstance().createReq(AwardsInfo.class)
                        .modifyAwards(awardID,title,content,id,url,startTime,endTime)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<HttpResult<Awards>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(HttpResult<Awards> awardsHttpResult) {
                                Toast.makeText(AwardsInfoActivity.this,awardsHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
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

            }
        };
        String uid = String.valueOf(id);
        CosManager.getInstance().uploadFile(path,uid,progressListener,resultListener);

    }

    private void intentInfo(){
        Intent intent = getIntent();
        awardID = Integer.parseInt(intent.getStringExtra("awardID"));
        String release = intent.getStringExtra("release");
        tvRelease.setText(release);
        RetrofitManager.getInstance().createReq(AwardsInfo.class)
                .getAwardsPubById(awardID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Awards>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Awards> awardsHttpResult) {
                        if (awardsHttpResult.getCode() == 200 && awardsHttpResult.getData() != null) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            releaseID = awardsHttpResult.getData().getReleaseID();
                            etTitle.setText(awardsHttpResult.getData().getAwardsTitle());
                            etContent.setText(awardsHttpResult.getData().getAwardsContent());
                            tvPath.setText(awardsHttpResult.getData().getWord());
                            url = awardsHttpResult.getData().getWord();
                            long startTime1 = awardsHttpResult.getData().getStartTime();
                            long endTime1 = awardsHttpResult.getData().getEndTime();
                            String startTime = df.format(startTime1);
                            String endTime = df.format(endTime1);
                            tvStarTime.setText(startTime);
                            tvEndTime.setText(endTime);
                            if (UserInfoManager.getInstance().getUid() == releaseID){
                                tvModify.setVisibility(View.VISIBLE);
                                tvSave.setVisibility(View.VISIBLE);
                                tvModifyFill.setVisibility(View.VISIBLE);
                            }else {
                                tvModify.setVisibility(View.GONE);
                                tvSave.setVisibility(View.GONE);
                                tvModifyFill.setVisibility(View.GONE);
                            }
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
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
    }

}
