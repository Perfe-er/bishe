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

    @FormUrlEncoded
    @POST("/infoEdit")
    Observable<HttpResult<User>> infoEdit(@Field("id") int id,@Field("stuID") String stuID,@Field("name") String name,@Field("sex") int sex,
                                          @Field("college") String college,@Field("parentPho") String parentPho,@Field("identity") String identity,
                                          @Field("address") String address,@Field("birthday") String birthday);
    @FormUrlEncoded
    @POST("/editStuType")
    Observable<HttpResult<User>> editStuType(@Field("id") int id,@Field("stuType") int stuType);

    @FormUrlEncoded
    @POST("/modifyPassWd")
    Observable<HttpResult<User>> modifyPassWd(@Field("id") int id,@Field("oldPwd") String oldPwd,@Field("newPwd") String newPwd);

    @FormUrlEncoded
    @POST("/modifyPassWdByPhone")
    Observable<HttpResult<User>> modifyPassWdByPhone(@Field("id") int id,@Field("phone") String phone,@Field("passWd") String passWd);

}
