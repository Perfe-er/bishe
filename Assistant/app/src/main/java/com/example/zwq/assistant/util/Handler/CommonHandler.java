package com.example.zwq.assistant.util.Handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

public class CommonHandler<T extends CommonDoHandler> extends Handler {
    private final WeakReference<T> mT;
    public CommonHandler(T t) {
        mT = new WeakReference<T>(t);
    }

    public CommonHandler(T t, Looper looper){
        super(looper);
        mT = new WeakReference<T>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        T t = mT.get();
        if (t != null) {
            t.doHandler(msg);
        }
    }
}