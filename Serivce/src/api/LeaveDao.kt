package com.hapi.api

import api.BaseDao
import been.*
import com.alibaba.fastjson.JSON
import com.hapi.classDao
import com.hapi.mSocketSever
import com.hapi.pageSize
import com.hapi.userDao
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import online.sanen.cdm.api.basic.Sorts
import online.sanen.cdm.api.condition.C

class LeaveDao : BaseDao() {


    public fun findLevelById(leaveID: Int): Leave? {
        val leave = Leave()
        leave.leaveID = leaveID

        val leaves = JdbcConnection.bootstrap.queryTable(Leave::class.java)
            .addCondition { c -> c.add(C.eq("leaveID", leaveID)) }
            .list(Leave::class.java)
        return if (leaves.isEmpty()) {
            null
        } else {
            leaves[0]
        }
    }


    /**
     * 根据id刷新获取请求详情
     */
    suspend fun leveDetails(call: ApplicationCall) {
        val request = call.request
        val leaveID: Int = request.queryParameters["leaveID"]?.toInt() ?: 0
        val leave = findLevelById(leaveID)
        writeGsonResponds(JSON.toJSONString(HttpResult(leave, 200, "发布成功")), call)

    }

    suspend fun listlLeve(call: ApplicationCall) {
        val request = call.request
        val stuID: Int = request.queryParameters["stuID"]?.toInt() ?: 0
        val page = request.queryParameters["page"]?.toInt() ?: 0
        val listlLeves =
            JdbcConnection.bootstrap.queryTable(Leave::class.java).limit(
                page * pageSize,
                pageSize
            )
                .sort(Sorts.DESC, "leaveID")
                .addCondition { c -> c.add(C.eq("stuID", stuID)) }
                .list(Leave::class.java)
        writeGsonResponds(JSON.toJSONString(HttpResult(listlLeves, 200, "成功")), call)

    }


    /**
     *
     */
    suspend fun createLeave(call: ApplicationCall) {

        val request = call.request

        val ratify: Int = 0  //1：批准，2：不批准
        val stuID: Int = request.queryParameters["stuID"]?.toInt() ?: 0
        val name: String? = request.queryParameters["name"]
        val sex: Int = request.queryParameters["sex"]?.toInt() ?: 0
        val reason: String? = request.queryParameters["reason"]
        val startDate: Long = request.queryParameters["startDate"]?.toLong() ?: 0   //时间戳
        val endDate: Long = request.queryParameters["endDate"]?.toLong() ?: 0


        val user = userDao.getUserById(stuID)

        if (user == null) {
            writeError("学生消息错误", 400, call)
            return
        }

        val clas = classDao.getClassByClassId(user.classID)
        if (clas == null) {
            writeError("班级消息错误", 400, call)
            return
        }

        val guider = userDao.getUserById(clas.founderId)
        if (guider == null) {
            writeError("找不到学生导员", 400, call)
            return
        }

        val leave = Leave()
        leave.ratifyID = guider.id
        leave.ratify = ratify
        leave.stuID = stuID
        leave.name = name
        leave.sex = sex
        leave.reason = reason
        leave.startDate = startDate
        leave.endDate = endDate
        leave.ratify = 0
        val id = JdbcConnection.bootstrap.query(leave).insert()
        leave.leaveID = id
        writeGsonResponds(JSON.toJSONString(HttpResult(leave, 200, "发布成功")), call)

        val msg =
            WebSocketMsg<Leave>(
                WebSocketMsg.WebSocktMsgLevelMsgCreate,
                leave,
                stuID,
                guider.id

            )
        mSocketSever.sendFramByUid(guider.id, msg)

    }

    suspend fun delLeave(call: ApplicationCall) {

        val request = call.request
        val ratify: Int = 0  //1：批准，2：不批准
        val leaveID: Int = request.queryParameters["leaveID"]?.toInt() ?: 0
        val uid: Int = request.queryParameters["uid"]?.toInt() ?: 0
        val leave = findLevelById(leaveID)
        if (leave == null) {
            writeError("找不到请假", 400, call)
            return
        }

        if (leave.stuID != uid) {
            writeError("uid错误", 400, call)
        }
        if (leave!!.ratify != 0) {
            writeError("请假已经处理不能修改", 400, call)
            return
        }

        JdbcConnection.bootstrap.query(leave).delete()
        writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(200, "删除成功")), call)
    }

    suspend fun ratifyLeave(call: ApplicationCall) {

        val request = call.request
        val leaveID: Int = request.queryParameters["leaveID"]?.toInt() ?: 0
        val uid: Int = request.queryParameters["uid"]?.toInt() ?: 0 //自己的id
        val ratify: Int = request.queryParameters["ratify"]?.toInt() ?: 0
        val reason = request.queryParameters["reason"]

        val leave = findLevelById(leaveID)
        if (leave == null) {
            writeError("找不到请假", 400, call)
            return
        }
        if (leave.ratifyID != uid) {
            writeError("uid错误", 400, call)
        }

        leave.ratify = ratify
        leave.reason = reason

        JdbcConnection.bootstrap.query(leave).setFields("ratify", "reason").update()
        writeGsonResponds(JSON.toJSONString(HttpResult(leave, 200, "审批成功")), call)

        val msg =
            WebSocketMsg<Leave>(
                WebSocketMsg.WebSocktMsgLevelMsgRatify,
                leave,
                uid,
                leave.stuID
            )
        mSocketSever.sendFramByUid(leave.stuID, msg)
    }


}