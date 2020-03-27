package api;

import been.HttpResult
import com.alibaba.fastjson.JSON
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.request.receiveParameters
import io.ktor.response.respondText


open class BaseDao {

    suspend fun writeGsonResponds(json:String, call: ApplicationCall){
        call.respondText(json, ContentType.Application.Json)
    }


    suspend fun writeError(msg:String,code:Int, call: ApplicationCall){
        writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(code, msg)), call)
    }

    suspend fun getParametersForm(call: ApplicationCall): Parameters {
        return call.receiveParameters()
    }



}