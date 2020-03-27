package com.example.zwq.assistant.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.example.zwq.assistant.util.Handler.CommonDoHandler;
import com.example.zwq.assistant.util.Handler.CommonHandler;

import androidx.fragment.app.Fragment;


/**
 * fragment基本类。
 */
public class BaseFragment extends Fragment implements View.OnClickListener, CommonDoHandler {

    protected CommonHandler<BaseFragment> fragmentHandler = new CommonHandler(this);

    @Override
    public void doHandler(Message msg) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {

    }

    public CommonHandler<BaseFragment> getFragmentHandler() {
        return fragmentHandler;
    }

}
