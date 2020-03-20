package com.example.zwq.assistant;

import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, CommonDoHandler {
    protected CommonHandler<BaseActivity> uiHandler;
    protected HttpAction httpAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpAction =HttpAction.getInstance();
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (uiHandler!=null){
            uiHandler.removeCallbacksAndMessages(null);
            uiHandler = null;
        }
    }

    @Override
    public void doHandler(Message msg) {

    }

    //私有方法区域
    private void init() {
        uiHandler = new CommonHandler<>(this);
    }
}
