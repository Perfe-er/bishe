package com.example.zwq.assistant.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.zwq.assistant.Adapter.AnnoPubClassAdapter;
import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.ActivityInfo;
import com.example.zwq.assistant.Service.ClassInfo;
import com.example.zwq.assistant.been.Activity;
import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActionPubActivity extends BaseActivity {
    EditText etTitle;
    EditText etContent;
    TextView tvPubAnno;
    TextView tvTime;
    ImageView ivReturn;
    RecyclerView rvSelectClass;
    AnnoPubClassAdapter adapter;
    LinearLayoutManager mLinearLayoutManager;
    List<Class> mClassList;
    private List<String> pubClass;
    private int year,month,day;
    private int hour,minute, seconds;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_action_pub);
        getDate();
        initView();
        setClassList();
    }

    public void initView(){
        rvSelectClass = findViewById(R.id.rvSelectClass);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        tvPubAnno = findViewById(R.id.tvPubAnno);
        tvTime = findViewById(R.id.tvTime);
        ivReturn = findViewById(R.id.ivReturn);
        tvPubAnno.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View view){
        DatePickerDialog.OnDateSetListener listener;
        TimePickerDialog.OnTimeSetListener listener1;
        DatePickerDialog dialog;
        TimePickerDialog dialog1;
        switch (view.getId()){
            case R.id.ivReturn:
                finish();
                break;
            case R.id.tvPubAnno:
                pubActivity();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pubActivity(){
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        int actFouID = UserInfoManager.getInstance().getUid();
        pubClass = adapter.getPubClass();
        String classID = String.join(",",pubClass);
        String time = tvTime.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date dateTime1 = null;
        try {
            dateTime1 = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateTime = dateTime1.getTime();
        RetrofitManager.getInstance().createReq(ActivityInfo.class)
                .pubActivity(title,content,actFouID,dateTime,classID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult<Activity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult<Activity> activityHttpResult) {
                        Toast.makeText(ActionPubActivity.this,activityHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                        if (activityHttpResult.getCode() == 200 ){
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
