package com.example.zwq.assistant.Service;

import android.text.TextUtils;

import com.example.zwq.assistant.manager.UserInfoManager;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeadInterceptor implements Interceptor {
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();


        Response response;
        if (!TextUtils.isEmpty(UserInfoManager.getInstance().getUserToken())) {
            //获取、修改请求头
            Headers headers = original.headers();
            Headers newHeader = headers.newBuilder()
                    .add("Authorization", "Bearer " + UserInfoManager.getInstance().getUserToken())
                    .build();
            Request.Builder builder = original.newBuilder()
                    .headers(newHeader);

            Request request = builder.build();

            response = chain.proceed(request);
        } else {
            response = chain.proceed(original);
        }

        return response;
    }
}
