package api

import been.Class
import been.HttpResult
import been.User
import com.alibaba.fastjson.JSON
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import io.ktor.request.ApplicationRequest
import io.ktor.request.receiveParameters
import kotlin.coroutines.Continuation
import online.sanen.cdm.api.condition.C

import java.util.ArrayList

class ClassDao : BaseDao() {


    suspend fun getClassByClassId(classID: Int): Class? {

        val cs =
            JdbcConnection.bootstrap.queryTable(Class::class.java).addCondition { c -> c.add(C.eq("classID", classID)) }
                .list(Class::class.java)

        var c: Class? = null
        if (!cs.isEmpty()) {
            c = cs[0]
        }
        return c
    }

    suspend   fun findClassByClassName(call: ApplicationCall) {
        val request = call.request
        val className = request.queryParameters["className"]//className
        val cs = JdbcConnection.bootstrap.queryTable(Class::class.java)
            .addCondition { c -> c.add(C.eq("className", className)) }.list(Class::class.java)

        var c: Class? = null
        if (!cs.isEmpty()) {
            c = cs[0]
        }
        writeGsonResponds(JSON.toJSONString(HttpResult<Class>(c, 200, "")), call)
    }

    /**
     * 创建班级
     *
     * @param call
     * @param continuation
     */
    suspend fun createClass(call: ApplicationCall) {
        val request = call.receiveParameters()
        val className = request["className"]//className
        val founderId = request["founderId"]//

        val c = Class()
        c.className = className
        c.founderID = Integer.parseInt(founderId!!)
        val cid = JdbcConnection.bootstrap.query(c).insert()
        c.classID = cid
        writeGsonResponds(JSON.toJSONString(HttpResult(c, 200, "创建成功")), call)
    }


    suspend  fun deleteClass(call: ApplicationCall) {

        val request = call.receiveParameters()
        val classID = request["classID"]//className

        val c = Class()
        c.classID = Integer.parseInt(classID!!)
        try {
            JdbcConnection.bootstrap.query(c).delete()
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(200, "删除成功")), call)
        } catch (e: Exception) {
            e.printStackTrace()
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "删除失败　" + e.message)), call)
        }

    }


   suspend fun modifyClass(call: ApplicationCall) {
        val request = call.receiveParameters()
        val classID = request["classID"]
        val className = request["className"]
        val c = Class()
        c.classID = Integer.parseInt(classID!!)
        c.className = className

        try {
            JdbcConnection.bootstrap.query(c).setFields("className").update()
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(200, "修改成功")), call)
        } catch (e: Exception) {
            e.printStackTrace()
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "修改失败　" + e.message)), call)
        }

    }

    suspend fun showClassByFounder(call: ApplicationCall){
        val request = call.request
        val founderID = request.queryParameters["founderID"]?.toInt()
        val classShow = ArrayList<Class>()
        val cs = JdbcConnection.bootstrap.queryTable(Class::class.java).addCondition { c ->
            c.add(C.eq("founderID", founderID))
        }.list(Class::class.java)
        classShow.addAll(cs)
        writeGsonResponds(JSON.toJSONString(HttpResult<List<Class>>(classShow, 200, "")), call)
    }

}
