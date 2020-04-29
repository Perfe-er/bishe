package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zwq.assistant.R;

public class ClassMoreActivity extends BaseActivity {
    ConstraintLayout conAnno;
    ConstraintLayout conAwards;
    ConstraintLayout conActivity;
    TextView tvClass;
    ImageView ivReturn;
    private int classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_class_more);
        initView();
        Intent intent = getIntent();
        classID = Integer.parseInt(intent.getStringExtra("classID"));
        tvClass.setText(intent.getStringExtra("className"));
    }

    public void initView(){
        conActivity = findViewById(R.id.conActivity);
        conAnno = findViewById(R.id.conAnno);
        conAwards = findViewById(R.id.conAwards);
        tvClass = findViewById(R.id.tvClass);
        ivReturn = findViewById(R.id.ivReturn);
        conActivity.setOnClickListener(this);
        conAnno.setOnClickListener(this);
        conAwards.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.conActivity:
                intent = new Intent(ClassMoreActivity.this,ClassActionActivity.class);
                intent.putExtra("classID",classID + "");
                startActivity(intent);
                break;
            case R.id.conAnno:
                intent = new Intent(ClassMoreActivity.this,ClassAnnoActivity.class);
                intent.putExtra("classID",classID + "");
                startActivity(intent);
                break;
            case R.id.conAwards:
                intent = new Intent(ClassMoreActivity.this,ClassAwardsActivity.class);
                intent.putExtra("classID",classID + "");
                startActivity(intent);
                break;
            case R.id.ivReturn:
                finish();
                break;
        }
    }
}
