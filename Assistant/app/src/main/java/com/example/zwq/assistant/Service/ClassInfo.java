package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.Class;
import com.example.zwq.assistant.been.HttpResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClassInfo {

    @GET("/showClassByFounder")
    Observable<HttpResult<List<Class>>> showClassByFounder(@Query("founderID") int founderID);
}
