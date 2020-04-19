package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.Awards;
import com.example.zwq.assistant.been.HttpResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AwardsInfo {

    @GET("/getListAwards")
    Observable<HttpResult<List<Awards>>> getListAwards(@Query("classID") int classID,@Query("page") int page);

    @FormUrlEncoded
    @POST("/deleteAwards")
    Observable<HttpResult<Awards>> deleteAwards(@Field("awardsID") int awardsID);

    @GET("/getAwardsPubById")
    Observable<HttpResult<Awards>> getAwardsPubById(@Query("awardsID") int awardsID);

    @FormUrlEncoded
    @POST("/modifyAwards")
    Observable<HttpResult<Awards>> modifyAwards(@Field("awardsID") int awardsID,@Field("awardsTitle") String awardsTitle,
                                                @Field("awardsContent") String awardsContent,
                                                @Field("releaseID") int releaseID,@Field("word") String word,
                                                @Field("startTime") long startTime,@Field("endTime") long endTime);

    @FormUrlEncoded
    @POST("/pubAwards")
    Observable<HttpResult<Awards>> pubAwards(@Field("releaseID") int releaseID,@Field("awardsTitle") String awardsTitle,
                                             @Field("awardsContent") String awardsContent,@Field("word") String word,
                                             @Field("startTime") long startTime,@Field("endTime") long endTime,
                                             @Field("toClass") String toClass);


}
