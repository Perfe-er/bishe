package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.Anno;
import com.example.zwq.assistant.been.HttpResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AnnoInfo {

    @GET("/listAnno")
    Observable<HttpResult<List<Anno>>> listAnno(@Query("classID") int classID, @Query("page") int page);
}
