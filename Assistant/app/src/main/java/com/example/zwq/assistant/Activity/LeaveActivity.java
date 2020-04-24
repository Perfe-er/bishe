package com.example.zwq.assistant.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.LeaveInfo;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Leave;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LeaveActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    EditText etName;
    EditText etReason;
    TextView tvStartTime;
    TextView tvEndTime;
    RadioGroup rgSex;
    RadioButton rbSexG;
    RadioButton rbSexM;
    ImageView ivReturn;
    Button btnYes;
    private int sex;
    private int year,month,day;
    private int hour,minute, seconds;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_leave);
        initView();
        getDate();
    }

    public void initView(){
        etName = findViewById(R.id.etName);
        etReason = findViewById(R.id.etReason);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        ivReturn = findViewById(R.id.ivReturn);
        rgSex = findViewById(R.id.rgSex);
        rbSexG = findViewById(R.id.rbSexG);
        rbSexM = findViewById(R.id.rbSexM);
        btnYes = findViewById(R.id.btnYes);
        btnYes.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        rgSex.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        DatePickerDialog.OnDateSetListener listener;
        TimePickerDialog.OnTimeSetListener listener1;
        DatePickerDialog dialog;
        TimePickerDialog dialog1;
        switch (v.getId()){
            case R.id.tvStartTime:
                listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                        tvStartTime.setText(y+"年"+ (++m) +"月"+d + "日 "+hour+":"+minute+":"+seconds);
                        year = y;
                        month = m;
                        day = d;
                    }
                };
                listener1 = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                        tvStartTime.setText(year+"年"+ (++month) +"月"+day + "日 "+hourOfDay+":"+min);
                        hour = hourOfDay;
                        minute = min;
                    }
                };
                dialog = new DatePickerDialog(this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog1 = new TimePickerDialog(this,0,listener1,hour,minute,true);
                dialog.show();
                dialog1.show();
                break;
            case R.id.tvEndTime:
                listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                        tvEndTime.setText(y+"年"+ (++m) +"月"+d + "日 "+hour+":"+minute+":"+seconds);
                        year = y;
                        month = m;
                        day = d;
                    }
                };
                listener1 = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                        tvEndTime.setText(year+"年"+ (++month) +"月"+day + "日 "+hourOfDay+":"+min);
                        hour = hourOfDay;
                        minute = min;
                    }
                };
                dialog = new DatePickerDialog(this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog1 = new TimePickerDialog(this,0,listener1,hour,minute,true);
                dialog.show();
                dialog1.show();
                break;
            case R.id.ivReturn:
                finish();
                break;
            case R.id.btnYes:
                applyLeave();
                break;
        }
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
    public void applyLeave(){
        int stuID = UserInfoManager.getInstance().getUid();
        String name = etName.getText().toString();
        String reason = etReason.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date startDate1 = null;
        try {
            startDate1 = sdf.parse(tvStartTime.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startTime = startDate1.getTime();
        Date endTime1 = null;
        try {
            endTime1 = sdf.parse(tvStartTime.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long endTime = endTime1.getTime();

        RetrofitManager.getInstance().createReq(LeaveInfo.class)
                .createLeave(stuID,name,sex,reason,startTime,endTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Leave>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Leave> leaveHttpResult) {
                        Toast.makeText(LeaveActivity.this,leaveHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                        if (leaveHttpResult.getCode() == 200){
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rbSexG:
                sex = 1;
                break;
            case R.id.rbSexM:
                sex = 2;
                break;
        }
    }
}
