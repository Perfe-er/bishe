package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ClassInfo {

    @GET("/showClassByFounder")
    Observable<HttpResult<List<Class>>> showClassByFounder(@Query("founderID") int founderID);

    @FormUrlEncoded
    @POST("/createClass")
    Observable<HttpResult<Class>> createClass(@Field("className") String className,@Field("founderID") int founderID);

    @FormUrlEncoded
    @POST("/deleteClass")
    Observable<HttpResult<Class>> deleteClass(@Field("classID") int classID);

    @GET("/findClassByClassName")
    Observable<HttpResult<Class>> findClassByClassName (@Query("className") String className);
}
