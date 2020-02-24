package com.blabla.api

import api.UserDao
import been.User
import com.alibaba.fastjson.JSON
import com.blabla.db.JdbcConnection
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import kotlin.coroutines.suspendCoroutine

suspend fun s()= suspendCoroutine<Any> {

}

fun userRoute(router: Route){
    router.apply {


        post("/login") {
            val c =suspendCoroutine<Any> {
                UserDao().login(call,it)
            }

//
//            val request = call.request
//            val queryParameters: Parameters = request.queryParameters
//            val param1: String? = request.queryParameters["param1"]
//
//            val list = JdbcConnection.bootstrap.queryTable<User>(User::class.java).list(User::class.java)
//            var s=""
//
//            list.forEach{
//                s+it.name
//                s+"  "+it.uid
//            }
//            call.respondText(JSON.toJSONString(list), ContentType.Application.Json)
        }

//        get("/listUser") {
//            UserDao().listUser(call,)
//
//        }
    }
}