package com.example.zwq.assistant.Service;


import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.User;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserInfo {

    /**
     * 登录
     * @param phone 手机号
     * @param passWd 密码
     * @return
     */
    @FormUrlEncoded
    @POST("/login")
    Observable<HttpResult<User>> login(@Field("phone") String phone, @Field("passWd") String passWd);

    /**
     * 注册
     * @param phone 手机号
     * @param passWd 密码

     * @return
     */
    @FormUrlEncoded
    @POST("/register")
    Observable<HttpResult<User>> register(@Field("phone") String phone,@Field("passWd") String passWd);


    @GET("/getUserInfoById")
    Observable<HttpResult<User>> getUserInfoById(@Query("id") int id);
}
