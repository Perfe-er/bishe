package com.example.zwq.assistant.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zwq.assistant.R;
import com.example.zwq.assistant.Service.AwardsInfo;
import com.example.zwq.assistant.been.AwardSign;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.manager.CosManager;
import com.example.zwq.assistant.manager.RetrofitManager;
import com.example.zwq.assistant.manager.UserInfoManager;
import com.tencent.cos.xml.listener.CosXmlProgressListener;

public class AwardSignActivity extends AppCompatActivity {
    ImageView ivReturn;
    TextView tvSelect;
    Button btnSign;
    EditText etFile;
    private int awardID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_award_sign);
        Intent intent = getIntent();
        awardID = Integer.parseInt(intent.getStringExtra("awardID"));
        ivReturn = findViewById(R.id.ivReturn);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSelect = findViewById(R.id.tvSelect);
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSystemFile();
            }
        });
        btnSign = findViewById(R.id.tvSign1);
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signAward();
            }
        });
        etFile = findViewById(R.id.etFile);
    }

    public void signAward(){
        long date = System.currentTimeMillis();
        String path = etFile.getText().toString();
        int uid = UserInfoManager.getInstance().getUid();
        CosXmlProgressListener progressListener = null;
        CosManager.ICosXmlResultListener listener = new CosManager.ICosXmlResultListener() {
            @Override
            public void onSuccess(String url) {
                RetrofitManager.getInstance().createReq(AwardsInfo.class)
                        .awardsSign(awardID,uid,url,date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<HttpResult<AwardSign>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(HttpResult<AwardSign> awardSignHttpResult) {
                                Toast.makeText(AwardSignActivity.this,awardSignHttpResult.getMsg(),Toast.LENGTH_SHORT).show();
                                if (awardSignHttpResult.getCode() == 200 ){
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

            @Override
            public void onFail(int code, String msg) {

            }
        };
        String id = String.valueOf(uid);
        CosManager.getInstance().uploadFile(path,id,progressListener,listener);
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
                    etFile.setText(path);
                }else {
                    Toast.makeText(AwardSignActivity.this,"文件不合法",Toast.LENGTH_SHORT).show();
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

}
