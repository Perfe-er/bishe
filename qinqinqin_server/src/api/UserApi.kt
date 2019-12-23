package com.blabla.api

import been.User
import com.alibaba.fastjson.JSON
import com.blabla.db.JdbcConnection
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get


fun userRoute(router: Route){
    router.apply {
        get("/user") {

            val list = JdbcConnection.bootstrap.queryTable<User>(User::class.java).list(User::class.java)
            var s=""

//            list.asIterable().forEach {
//
//            }
            list.forEach{
                s+it.name
                s+"  "+it.uid
            }
            call.respondText(JSON.toJSONString(list), ContentType.Application.Json)
        }
    }
}