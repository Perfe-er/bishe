package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Leave;

import java.util.List;

import io.reactivex.Observable;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LeaveInfo {

    @FormUrlEncoded
    @POST("/createLeave")
    Observable<HttpResult<Leave>> createLeave(@Field("stuID") int stuID, @Field("name") String name,
                                              @Field("sex") int sex, @Field("reason") String reason,
                                              @Field("startDate") long startDate, @Field("endDate") long endDate);

    @GET("/listLeaveByRatifyID")
    Observable<HttpResult<List<Leave>>> listLeaveByRatifyID(@Query("ratifyID") int ratifyID,@Query("page") int page);

    @GET("/listLeaveByClassID")
    Observable<HttpResult<List<Leave>>> listLeaveByClassID(@Query("classID") int classID,@Query("page") int page);

    @GET("/listlLeve")
    Observable<HttpResult<List<Leave>>> listlLeve(@Query("stuID") int stuID,@Query("page") int page);

    @GET("/leveDetails")
    Observable<HttpResult<Leave>> leveDetails(@Query("leaveID") int leaveID);

    @FormUrlEncoded
    @POST("/ratifyLeave")
    Observable<HttpResult<Leave>> ratifyLeave(@Field("leaveID") int leaveID,@Field("uid") int uid,@Field("ratify") int ratify);

    @FormUrlEncoded
    @POST("/delLeave")
    Observable<HttpResult<Leave>> delLeave(@Field("leaveID") int leaveID,@Field("uid") int uid);
}
