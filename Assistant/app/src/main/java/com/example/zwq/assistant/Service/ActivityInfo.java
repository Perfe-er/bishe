package com.example.zwq.assistant.Service;

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
    @GET("/listActivity")
    Observable<HttpResult<List<Activity>>> listActivity(@Query("classID") int classID, @Query("page") int page);

    @FormUrlEncoded
    @POST("/modifyActivity")
    Observable<HttpResult<Activity>> modifyActivity(@Field("actID") int actID, @Field("actTitle") String actTitle,
                                                    @Field("actContent") String actContent, @Field("actFouID") int actFouID,
                                                    @Field("actDate") Long actDate);

    @FormUrlEncoded
    @POST("/deleteActivity")
    Observable<HttpResult<Activity>> deleteActivity(@Field("actID") int actID);

    @FormUrlEncoded
    @POST("/pubActivity")
    Observable<HttpResult<Activity>> pubActivity(@Field("actTitle") String actTitle,@Field("actContent") String actContent, @Field("actFouID") int actFouID,
                                         @Field("actDate") Long actDate,@Field("classIDs") String classIDs);

    @FormUrlEncoded
    @POST("/signActivity")
    Observable<HttpResult<Activity>> signActivity(@Field("stuID") int stuID,@Field("actID") int actID,
                                                  @Field("sign") int sign,@Field("signDate") long signDate,@Field("classID") int classID);
}
