package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Sign;
import com.example.zwq.assistant.been.SignRecord;
import com.example.zwq.assistant.been.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SignInfo {
    @FormUrlEncoded
    @POST("/pubSign")
    Observable<HttpResult<Sign>> pubSign(@Field("originID") int originID, @Field("iniDate") long iniDate,
                                         @Field("signcol") String signcol,@Field("toClass") String toClass);

    @FormUrlEncoded
    @POST("/endSign")
    Observable<HttpResult<Sign>> endSign(@Field("signID") int signID,@Field("endDate") long endDate);

    @GET("/getSiginOfUserRecev")
    Observable<HttpResult<List<SignRecord>>> getSiginOfUserRecev(@Query("uid") int uid, @Query("page") int page);

    @GET("/getSiginOfUserPub")
    Observable<HttpResult<List<Sign>>> getSiginOfUserPub(@Query("uid") int uid, @Query("page") int page);

    @GET("/getSignedUser")
    Observable<HttpResult<List<User>>> getSignedUser(@Query("signID") int signID, @Query("type") int type);

    @FormUrlEncoded
    @POST("/sign")
    Observable<HttpResult<SignRecord>> sign(@Field("signID") int signID,@Field("uid") int uid,@Field("signDate") long signDate);

}
