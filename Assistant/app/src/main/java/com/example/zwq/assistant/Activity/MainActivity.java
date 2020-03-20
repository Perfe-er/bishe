package com.example.zwq.assistant.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.zwq.assistant.R;
import com.mob.MobSDK;

public class MainActivity extends BaseActivity {
    Button btnStudent;
    Button btnAssistant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobSDK.submitPolicyGrantResult(true, null);

        btnStudent =findViewById(R.id.btnStudent);
        btnAssistant = findViewById(R.id.btnAssistant);
        btnStudent.setOnClickListener(this);
        btnAssistant.setOnClickListener(this);
    }

    public void onClick(View v){
        super.onClick(v);
        Intent intent;
        switch (v.getId()){
            case R.id.btnStudent:

        }
    }
}
