package com.example.zwq.assistant.Service;

import com.example.zwq.assistant.been.HttpResult;
import com.example.zwq.assistant.been.Moral;
import com.example.zwq.assistant.been.MoralReceive;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MoralInfo {
    @POST("/createMoral")
    @FormUrlEncoded
    Observable<HttpResult<Moral>> createMoral(@Field("ids") String ids,@Field("changeP") int changeP,
                                              @Field("reason") String reason,@Field("fine") double fine,
                                              @Field("add") double add,@Field("dateTime") long dateTime);

    @GET("/moralRecord")
    Observable<HttpResult<List<Moral>>> moralRecord(@Query("stuID") int stuID);
}
