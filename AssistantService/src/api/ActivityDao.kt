package com.example.api

import api.BaseDao
import been.*
import com.alibaba.fastjson.JSON
import com.example.pageSize
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import online.sanen.cdm.api.basic.Sorts
import online.sanen.cdm.api.condition.C
import org.apache.http.util.TextUtils
import java.util.ArrayList

class ActivityDao : BaseDao() {

    fun getReceive(actID:Int,classID:Int): ActReceive? {
        val recs =JdbcConnection.bootstrap.queryTable(ActReceive::class.java).addCondition { c ->
            c.add(C.eq("actID", actID))
            c.add(C.eq("classID",classID))}.list(ActReceive::class.java)
        return if (recs.isEmpty()) {
            null
        } else {
            recs[0]
        }
    }

    fun getSignerByStudent(classID : Int,receiveID : Int) : List<ActSign>? {
            val signs = JdbcConnection.bootstrap.queryTable(ActSign::class.java).addCondition{ c ->
                c.add(C.eq("classID", classID))
                c.add(C.eq("receiveID", receiveID))
            }.list(ActSign::class.java)
        println("getSignerByStudent" + classID + " " + signs.size)
        return signs
    }

    fun getSignerByAssistant(receiveID : Int) : List<ActSign>? {
        val signs = JdbcConnection.bootstrap.queryTable(ActSign::class.java).addCondition{ c ->
            c.add(C.eq("receiveID", receiveID))
        }.list(ActSign::class.java)
        println("getSignerByAssistant" + receiveID + " " + signs.size)
        return signs
    }

    suspend fun signList(call: ApplicationCall){
        val request = call.request
        val classID :Int = request.queryParameters["classID"]?.toInt()?:0
        val actID:Int = request.queryParameters["actID"]?.toInt()?:0
        var receiveID:Int = getReceive(actID,classID)!!.receiveID
        val stuType = request.queryParameters["stuType"]?.toInt()
        if (stuType == 2){
            val signs = getSignerByAssistant(receiveID)
            writeGsonResponds(JSON.toJSONString(HttpResult<List<ActSign>>(signs,200,"")),call)
        }else{
            val signs = getSignerByStudent(classID,receiveID)
            writeGsonResponds(JSON.toJSONString(HttpResult<List<ActSign>>(signs,200,"")),call)
        }
    }

    /**
     * 活动报名
     */
    suspend fun signActivity(call: ApplicationCall){
        val request = call.receiveParameters()
        val stuID = request["stuID"]?.toInt()
        val sign = request["sign"]?.toInt()
        val signDate = request["signDate"]?.toLong()
        val classID:Int = request["classID"]?.toInt()?:0
        val actID:Int = request["actID"]?.toInt()?:0
        var receiveID:Int = getReceive(actID,classID)!!.receiveID
        val actSign = ActSign()
        actSign.receiveID = receiveID
        actSign.stuID = stuID?:0
        actSign.sign = sign?:0
        actSign.signDate = signDate?:0
        actSign.classID = classID
        JdbcConnection.bootstrap.query(actSign).insert()
        writeGsonResponds(JSON.toJSONString(HttpResult(actSign, 200, "报名成功")), call)
    }
    /**
     * 活动列表
     */
    suspend fun listActivity(call: ApplicationCall) {

        val request = call.request
        val classID = request.queryParameters["classID"]?.toInt()
        val page = request.queryParameters["page"]?.toInt() ?: 0
        val actRes = ArrayList<Activity>();
        val actReceives =
            JdbcConnection.bootstrap.queryTable(ActReceive::class.java).limit(
                page * pageSize,
                pageSize
            )
                .sort(Sorts.DESC, "receiveID")
                .addCondition { c -> c.add(C.eq("classID", classID)) }
                .list(ActReceive::class.java)
        actReceives?.forEach {
            val acts =
                JdbcConnection.bootstrap.queryTable(Activity::class.java)
                    .addCondition { c -> c.add(C.eq("actID", it.actID)) }
                    .list(Activity::class.java)
            actRes.addAll(acts)
        }

        writeGsonResponds(JSON.toJSONString(HttpResult(actRes, 200, "成功")), call)

    }

    /**
     * 搜索
     */
    suspend fun searchActivity(call: ApplicationCall) {

        val request = call.request
        val keyWorlds = request.queryParameters["keyWorlds"]
        val actRes = ArrayList<Activity>()
        if (!TextUtils.isEmpty(keyWorlds)) {
            val acts =
                JdbcConnection.bootstrap.queryTable(Activity::class.java)

                    .addCondition { c -> c.add(C.contains("actTitle", keyWorlds)) }
                    .limit(0, pageSize)
                    .list(Activity::class.java)
            actRes.addAll(acts)
        }
        writeGsonResponds(JSON.toJSONString(HttpResult(actRes, 200, "成功")), call)

    }

    suspend fun pubActivity(call: ApplicationCall) {
        val request = call.receiveParameters()
        val actTitle = request["actTitle"]
        val actContent = request["actContent"]
        val actFouID = request["actFouID"]
        val actDate = request["actDate"]
        val classIDs = request["classIDs"]//接收班级id classIDs  ->  "1,2,3,4,7" 中间用,分割
        val activity = Activity()

        if (TextUtils.isEmpty(actTitle)
            || TextUtils.isEmpty(actContent)
            || TextUtils.isEmpty(actFouID)
            || TextUtils.isEmpty(actDate)
            || TextUtils.isEmpty(classIDs)
        ) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "参数有误")), call)
        } else {
            activity.actTitle = actTitle
            activity.actContent = actContent
            activity.actFouID = Integer.parseInt(actFouID!!)
            activity.actDate = java.lang.Long.parseLong(actDate!!)
            val id = JdbcConnection.bootstrap.query(activity).insert()
            activity.actID = id
            val receives = ArrayList<ActReceive>()
            val recs = classIDs!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val len = recs.size

            activity.classIDs = recs.asList()
            for (i in 0 until len - 1) {
                val cid = Integer.parseInt(recs[i])
                val receive = ActReceive()
                receive.actID = id
                receive.classID = cid
                receives.add(receive)

            }

            JdbcConnection.bootstrap.query(receives).insert()
            writeGsonResponds(JSON.toJSONString(HttpResult(activity, 200, "发布成功")), call)
        }

    }

    suspend fun deleteActivity(call: ApplicationCall) {

        val request = call.receiveParameters()
        val actID = request["actID"]?.toInt()
        val activity = Activity()
        activity.actID = actID ?: 0
        JdbcConnection.bootstrap.query(activity).delete()

        writeGsonResponds(JSON.toJSONString(HttpResult(activity, 200, "删除")), call)
    }


    suspend fun modifyActivity(call: ApplicationCall) {

        val request = call.receiveParameters()
        val actID = request["actID"]?.toInt()
        val actTitle = request["actTitle"]
        val actContent = request["actContent"]
        val actFouID = request["actFouID"]?.toInt()
        val actDate1 = request["actDate"]
        val actDate:Long = actDate1?.toLong()?:0
        val activity = Activity()
        activity.actID = actID ?: 0
        activity.actContent = actContent
        activity.actTitle = actTitle
        activity.actFouID = actFouID ?:0
        activity.actDate = actDate
        try {
            JdbcConnection.bootstrap.query(activity).setFields("actTitle", "actContent","actFouID","actDate").update()
            writeGsonResponds(JSON.toJSONString(HttpResult(activity, 200, "更新成功")), call)
        } catch (e: Exception) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "更新失败${e.message}")), call)
        }


    }
}