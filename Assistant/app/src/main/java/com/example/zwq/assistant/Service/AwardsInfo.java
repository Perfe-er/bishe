package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.ActSign;
import com.example.zwq.assistant.been.AwardSign;
import com.example.zwq.assistant.been.AwardSignComment;
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

    @FormUrlEncoded
    @POST("/deleteAwardsSign")
    Observable<HttpResult<AwardSign>> deleteAwardsSign(@Field("awardSignID") int awardSignID);

    @FormUrlEncoded
    @POST("/modifyAwardsSign")
    Observable<HttpResult<AwardSign>> modifyAwardsSign(@Field("awardSignID") int awardSignID, @Field("word") String word,
                                                     @Field("date") long date);
    @GET("/searchAwards")
    Observable<HttpResult<List<Awards>>> searchAwards(@Query("keyWorlds") String keyWorlds);

    @GET("/getListAwards")
    Observable<HttpResult<List<Awards>>> getListAwards(@Query("classID") int classID,@Query("page") int page);

    @GET("/ListAwards")
    Observable<HttpResult<List<Awards>>> ListAwards(@Query("releaseID") int releaseID);

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

    @FormUrlEncoded
    @POST("/awardsSign")
    Observable<HttpResult<AwardSign>> awardsSign(@Field("awardsPubId") int awardsPubId,@Field("uid") int uid,
                                                 @Field("word") String word,@Field("date") long date);

    @FormUrlEncoded
    @POST("/editPass")
    Observable<HttpResult<AwardSign>> editPass(@Field("awardSignID") int awardSignID);

    @GET("/listAwardSignComment")
    Observable<HttpResult<List<AwardSignComment>>> listAwardSignComment(@Query("awardSignID") int awardSignID);

    @GET("/getAwardsSignById")
    Observable<HttpResult<AwardSign>> getAwardsSignById(@Query("awardSignID") int awardSignID);

    @GET("/getawardsSignOfPub")
    Observable<HttpResult<List<AwardSign>>> getawardsSignOfPub(@Query("awardsID") int awardsID);

    @GET("/listMySignById")
    Observable<HttpResult<List<AwardSign>>> listMySignById(@Query("uid") int uid,@Query("page") int page);

    @FormUrlEncoded
    @POST("/awardsComment")
    Observable<HttpResult<AwardSignComment>> awardsComment(@Field("uid") int uid,@Field("awardSignID") int awardSignID,
                                                           @Field("commentContent") String commentContent);
}
