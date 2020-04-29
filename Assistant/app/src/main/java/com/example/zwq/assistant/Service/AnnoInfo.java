package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Leave;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AnnoInfo {

    @GET("/listAnno")
    Observable<HttpResult<List<Anno>>> listAnno(@Query("classID") int classID, @Query("page") int page);

    @FormUrlEncoded
    @POST("/modifyAnno")
    Observable<HttpResult<Anno>> modifyAnno(@Field("annoID") int annoID,@Field("annoTitle") String annoTitle, @Field("content") String content,
                                            @Field("releaseID") int releaseID,@Field("releDate") Long releDate);

    @FormUrlEncoded
    @POST("/deleteAnno")
    Observable<HttpResult<Anno>> deleteAnno(@Field("annoID") int annoID);

    @FormUrlEncoded
    @POST("/pubAnno")
    Observable<HttpResult<Anno>> pubAnno(@Field("annoTitle") String annoTitle,@Field("content") String content, @Field("releaseID") int releaseID,
                                         @Field("releDate") Long releDate,@Field("classIDs") String classIDs);

    @GET("/searchAnno")
    Observable<HttpResult<List<Anno>>> searchAnno(@Query("classID") int classID,@Query("keyWorlds") String keyWorlds);

}
