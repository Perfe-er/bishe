package com.hapi.api

import api.BaseDao
import been.*
import com.alibaba.fastjson.JSON
import com.example.mSocketSever
import com.example.pageSize
import com.example.userDao
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import online.sanen.cdm.api.basic.Sorts
import online.sanen.cdm.api.condition.C
import org.apache.http.util.TextUtils
import java.util.ArrayList

class SignDao : BaseDao() {


    fun getSiginByClassId(classID: Int, page: Int): List<Sign> {

        val ls = JdbcConnection.bootstrap.queryTable(SignReceive::class.java)
            .addCondition { c -> c.add(C.eq("classID", classID)) }
            .limit(page * pageSize, pageSize)
            .sort(Sorts.DESC, "receiveID")
            .list(SignReceive::class.java)
        val signs = ArrayList<Sign>()

        ls?.forEach {
            getSiginById(it.signID)?.let {
                signs.add(it)
            }
        }
        return signs
    }

    fun getSiginById(signID: Int): Sign? {

        val sign =
            JdbcConnection.bootstrap.queryTable(Sign::class.java).addCondition { c -> c.add(C.eq("signID", signID)) }
                .list(Sign::class.java)
        return if (sign.isEmpty()) {
            null
        } else {
            val sign = sign[0]
            val receives = JdbcConnection.bootstrap.queryTable(SignReceive::class.java)
                .addCondition { c -> c.add(C.eq("signID", signID)) }
                .list(SignReceive::class.java)
            val recId = ArrayList<String>()

            receives?.forEach {
                recId.add(it.classID.toString())
            }
            sign.toClass = recId
            return sign
        }
    }

    suspend fun getSiginById(call: ApplicationCall) {
        val request = call.request
        val signID: Int = request.queryParameters["signID"]?.toInt() ?: 0
        val sign = getSiginById(signID)
        writeGsonResponds(JSON.toJSONString(HttpResult<Sign>(sign, 200, "")), call)
    }



    suspend fun getSignedUser(call: ApplicationCall) {
        val request = call.request
        val signID: Int = request.queryParameters["signID"]?.toInt() ?: 0
        val type: Int = request.queryParameters["type"]?.toInt() ?: 0 //1已经签到的人　２还没签到的人

        val sign = getSiginById(signID)
        if(sign==null){
            writeError("id错误", 400, call)
            return
        }

        val recClass = JdbcConnection.bootstrap.queryTable(SignReceive::class.java)
            .addCondition { c -> c.add(C.eq("classID", sign.signID)) }
            .list(SignReceive::class.java)

        val usersResult = ArrayList<User>()

        recClass?.forEach {
            val users = userDao.getStudentByClassId(it.classID)
            users?.forEach {
                val signRecords = JdbcConnection.bootstrap.queryTable(SignRecord::class.java).addCondition { c ->
                    c.add(C.eq("uid", it.id))
                    c.add(C.eq("signId", signID))
                }.list(SignRecord::class.java)

                if(type==1){
                    if(!signRecords.isEmpty()){
                        usersResult.add(it)
                    }
                }else{
                    if(signRecords.isEmpty()){
                        usersResult.add(it)
                    }
                }
            }
        }
        writeGsonResponds(JSON.toJSONString(HttpResult<List<User>>(usersResult, 200, "")), call)

    }


    suspend fun getSiginOfUserRecev(call: ApplicationCall) {
        val request = call.request
        val uid: Int = request.queryParameters["uid"]?.toInt() ?: 0
        val page: Int = request.queryParameters["page"]?.toInt() ?: 0
        val user = userDao.getUserById(uid)
        if (user == null) {
            writeError("id错误", 400, call)
            return
        }
        val clasId = user.classID
        val signs = getSiginByClassId(clasId, page)

        val sinRecodResult = ArrayList<SignRecord>()

        signs?.forEach { si ->
            val signRecords = JdbcConnection.bootstrap.queryTable(SignRecord::class.java).addCondition { c ->
                c.add(C.eq("uid", uid))
                c.add(C.eq("signId", si.signID))
            }.list(SignRecord::class.java)

            val signRecord =
                if (signRecords.isEmpty()) {
                    SignRecord().apply {
                        this.signId = si.signID
                        this.signStatus = 2
                        this.sign = si
                    }
                } else {
                    signRecords[0].apply {
                        this.sign = si
                    }
                }
            sinRecodResult.add(signRecord)
        }

        writeGsonResponds(JSON.toJSONString(HttpResult<List<SignRecord>>(sinRecodResult, 200, "")), call)
    }

    suspend fun getSiginOfUserPub(call: ApplicationCall) {
        val request = call.request
        val uid: Int = request.queryParameters["uid"]?.toInt() ?: 0
        val page: Int = request.queryParameters["page"]?.toInt() ?: 0
        val user = userDao.getUserById(uid)
        if (user == null) {
            writeError("id错误", 400, call)
            return
        }

        val ls = JdbcConnection.bootstrap.queryTable(Sign::class.java)
            .addCondition { c -> c.add(C.eq("originID", uid)) }
            .limit(page * pageSize, pageSize)
            .sort(Sorts.DESC, "signID")
            .list(Sign::class.java)
        writeGsonResponds(JSON.toJSONString(HttpResult<List<Sign>>(ls, 200, "")), call)
    }


    suspend fun sign(call: ApplicationCall) {
        val request = call.receiveParameters()
        val uid: Int = request["uid"]?.toInt() ?: 0
        val signId: Int = request["signId"]?.toInt() ?: 0
        val signDate = request["signDate"]?.toLong() ?: 0

        val sign = getSiginById(signId)
        if (sign == null) {
            writeError("id错误", 400, call)
        }
        val signRecord = SignRecord()
        signRecord.signDate = signDate
        signRecord.signId = signId
        signRecord.uid = uid
        signRecord.signStatus = 1
        val rId = JdbcConnection.bootstrap.query(signRecord).insert()
        signRecord.signRecordId = rId
        signRecord.sign = sign
        writeGsonResponds(JSON.toJSONString(HttpResult<SignRecord>(signRecord, 200, "")), call)
    }


    suspend fun pubSign(call: ApplicationCall) {

        val request = call.receiveParameters()
        val originID: Int = request["originID"]?.toInt() ?: 0
        val iniDate: Long = request["iniDate"]?.toLong() ?: 0
        val endDate: Long = request["endDate"]?.toLong() ?: 0
        val signcol: String = request["signcol"] ?: ""
        val toClass: String = request["toClass"] ?: "" //接受班级　"1,2,4,6"

        if (TextUtils.isEmpty(toClass)
            || TextUtils.isEmpty(signcol)
        ) {
            writeError("参数有误", 400, call)
        }


        val recs = toClass.split(",").toList()
        val len = recs.size

        val sign = Sign()

        sign.iniDate = iniDate;
        sign.signcol = signcol
        sign.endDate = endDate;
        sign.toClass = recs
        sign.originID = originID

        val id = JdbcConnection.bootstrap.query(sign).insert()
        sign.signID = id

        writeGsonResponds(JSON.toJSONString(HttpResult<Sign>(sign, 200, "")), call)

        recs.forEach {
            val receive = SignReceive()
            receive.signID = id
            receive.classID = it.toInt()
            val receiveId = JdbcConnection.bootstrap.query(receive).insert()
            receive.receiveID = receiveId
            val users = userDao.getStudentByClassId(receive.classID)
            users?.forEach { user ->
                val msg =
                    WebSocketMsg<Sign>(
                        WebSocketMsg.WebSocktMsgLevelMsgPubSign,
                        sign,
                        originID,
                        user.id
                    )

                mSocketSever.sendFramByUid(user.id, msg)
            }
        }
    }

}