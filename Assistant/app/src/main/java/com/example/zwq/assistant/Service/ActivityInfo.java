package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.ActSign;
import com.example.zwq.assistant.been.Activity;
import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.HttpResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ActivityInfo {
    @GET("/searchActivity")
    Observable<HttpResult<List<Activity>>> searchActivity(@Query("keyWorlds") String keyWorlds);

    @GET("/listActivity")
    Observable<HttpResult<List<Activity>>> listActivity(@Query("classID") int classID, @Query("page") int page);

    @GET("/assistantList")
    Observable<HttpResult<List<Activity>>> assistantList(@Query("actFouID") int actFouID);

    @FormUrlEncoded
    @POST("/modifyActivity")
    Observable<HttpResult<Activity>> modifyActivity(@Field("actID") int actID, @Field("actTitle") String actTitle,
                                                    @Field("actContent") String actContent, @Field("actFouID") int actFouID,
                                                    @Field("actDate") Long actDate);

    @FormUrlEncoded
    @POST("/deleteActivity")
    Observable<HttpResult<Activity>> deleteActivity(@Field("actID") int actID);

    @FormUrlEncoded
    @POST("/deleteSign")
    Observable<HttpResult<ActSign>> deleteSign(@Field("actSignID") int actSignID);

    @FormUrlEncoded
    @POST("/pubActivity")
    Observable<HttpResult<Activity>> pubActivity(@Field("actTitle") String actTitle,@Field("actContent") String actContent, @Field("actFouID") int actFouID,
                                         @Field("actDate") Long actDate,@Field("classIDs") String classIDs);

    @FormUrlEncoded
    @POST("/signActivity")
    Observable<HttpResult<Activity>> signActivity(@Field("stuID") int stuID,@Field("actID") int actID,
                                                  @Field("sign") int sign,@Field("signDate") long signDate,@Field("classID") int classID);

    @GET("/signList")
    Observable<HttpResult<List<ActSign>>> signList(@Query("classID") int classID, @Query("actID") int actID,@Query("stuType") int stuType);
}
