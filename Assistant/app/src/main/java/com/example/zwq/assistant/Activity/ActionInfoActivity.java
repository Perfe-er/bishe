package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ActivityInfo;
import com.example.zwq.assistant.Service.AnnoInfo;
import com.example.zwq.assistant.been.Activity;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActionInfoActivity extends BaseActivity {
    ImageView ivReturn;
    TextView tvSave;
    TextView tvModify;
    TextView tvRelease;
    TextView tvTime;
    TextView tvSign;
    EditText etTitle;
    EditText etContent;
    Button btnSign;
    private int year,month,day;
    private int hour,minute, seconds;
    Calendar calendar;
    private int actID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_action_info);
        initView();
        intentAnno();
        getDate();
    }

    public void initView(){
        ivReturn = findViewById(R.id.ivReturn);
        tvSave = findViewById(R.id.tvSave);
        tvModify = findViewById(R.id.tvModify);
        tvRelease = findViewById(R.id.tvRelease);
        tvTime = findViewById(R.id.tvTime);
        tvSign = findViewById(R.id.tvSign);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSign = findViewById(R.id.btnSign);
        ivReturn.setOnClickListener(this);
        tvModify.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        tvSign.setOnClickListener(this);
        btnSign.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        int userType = UserInfoManager.getInstance().getLoginUser().getStuType();
        if (userType == 0){
            tvModify.setVisibility(View.GONE);
            tvSave.setVisibility(View.GONE);
            btnSign.setVisibility(View.VISIBLE);
        }else if (userType == 1){
            tvModify.setVisibility(View.VISIBLE);
            tvSave.setVisibility(View.VISIBLE);
            btnSign.setVisibility(View.VISIBLE);
        }else{
            tvModify.setVisibility(View.VISIBLE);
            tvSave.setVisibility(View.VISIBLE);
            btnSign.setVisibility(View.GONE);
        }
    }

    public void onClick(View view){
        DatePickerDialog.OnDateSetListener listener;
        TimePickerDialog.OnTimeSetListener listener1;
        DatePickerDialog dialog;
        TimePickerDialog dialog1;
        switch (view.getId()){
            case R.id.tvSave:
                editActivity();
                finish();
                break;
            case R.id.tvSign:
                Intent intent = new Intent(this,ActionSignActivity.class);
                intent.putExtra("actID",actID + "");
                startActivity(intent);
                break;
            case R.id.tvModify:
                etTitle.setEnabled(true);
                etContent.setEnabled(true);
                tvModify.setTextColor(Color.parseColor("#ABABAB"));
                break;
            case R.id.btnSign:
                stuSign();
                break;
            case R.id.ivReturn:
                finish();
                break;
            case R.id.tvTime:
                listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                        tvTime.setText(y+"年"+ (++m) +"月"+d + "日 "+hour+":"+minute+":"+seconds);
                        year = y;
                        month = m;
                        day = d;
                    }
                };
                listener1 = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                        tvTime.setText(year+"年"+ (++month) +"月"+day + "日 "+hourOfDay+":"+min);
                        hour = hourOfDay;
                        minute = min;
                    }
                };
                dialog = new DatePickerDialog(this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog1 = new TimePickerDialog(this,0,listener1,hour,minute,true);
                dialog.show();
                dialog1.show();
                break;
        }
    }

    public void stuSign(){
        int classID = UserInfoManager.getInstance().getLoginUser().getClassID();
        int stuID = UserInfoManager.getInstance().getUid();
        long signDate = System.currentTimeMillis();
        RetrofitManager.getInstance().createReq(ActivityInfo.class)
                .signActivity(stuID,actID,1,signDate,classID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Activity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Activity> activityHttpResult) {
                        if (activityHttpResult.getCode() == 200 ){
                            btnSign.setBackgroundResource(R.drawable.shape_login_no);
                            finish();
                            Toast.makeText(ActionInfoActivity.this,"报名成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ActionInfoActivity.this,"报名失败",Toast.LENGTH_SHORT).show();
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
    public void editActivity(){
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        String time = tvTime.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date dateTime1 = null;
        try {
            dateTime1 = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateTime = dateTime1.getTime();
        int actFouID = UserInfoManager.getInstance().getUid();
        RetrofitManager.getInstance().createReq(ActivityInfo.class)
                .modifyActivity(actID,title,content,actFouID,dateTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Activity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Activity> activityHttpResult) {
                        Toast.makeText(ActionInfoActivity.this,activityHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void intentAnno(){
        Intent intent = getIntent();
        etTitle.setText(intent.getStringExtra("title"));
        etContent.setText(intent.getStringExtra("content"));
        tvRelease.setText(intent.getStringExtra("release"));
        tvTime.setText(intent.getStringExtra("dateTime"));
        actID = Integer.valueOf(intent.getStringExtra("actID"));
    }

    public void getDate(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);//获取当前年
        month = calendar.get(Calendar.MONTH);//月份从0开始计算的
        day = calendar.get(Calendar.DATE);//获取日
        hour = calendar.get(Calendar.HOUR);//获取小时
        minute = calendar.get(Calendar.MINUTE);//获取分钟
        seconds = calendar.get(Calendar.SECOND);//获取秒钟
    }
}
