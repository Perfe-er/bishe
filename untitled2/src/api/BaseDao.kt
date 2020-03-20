package api;

import com.alibaba.fastjson.JSON
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText


open class BaseDao {

    suspend fun writeGsonResponds(json:String, call: ApplicationCall){
        call.respondText(json, ContentType.Application.Json)
    }

}