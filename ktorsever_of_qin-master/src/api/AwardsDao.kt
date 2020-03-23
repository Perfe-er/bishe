package com.hapi.api

import api.BaseDao
import been.*
import com.alibaba.fastjson.JSON
import com.hapi.mSocketSever
import com.hapi.pageSize
import com.hapi.userDao
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import online.sanen.cdm.api.basic.Sorts
import online.sanen.cdm.api.condition.C


class AwardsDao : BaseDao() {


    fun getAwardsSignById(awardSignID: Int): AwardSign? {
        val awardSign = JdbcConnection.bootstrap.queryTable(AwardSign::class.java)
            .addCondition { c -> c.add(C.eq("awardSignID", awardSignID)) }
            .list(AwardSign::class.java)
        return if (awardSign.isEmpty()) {
            null
        } else {
            val sig = awardSign[0]
            val pub = getawardsPubById(sig.awardsPubId)
            sig.awardsPub = pub
            return sig
        }
    }

    fun getawardsPubById(awardsID: Int): AwardsPub? {
        val awardsPub = JdbcConnection.bootstrap.queryTable(AwardsPub::class.java)
            .addCondition { c -> c.add(C.eq("awardsID", awardsID)) }
            .list(AwardsPub::class.java)
        return if (awardsPub.isEmpty()) {
            null
        } else {
            awardsPub[0]
        }
    }

    suspend fun getawardsSignOfPub(call: ApplicationCall) {
        val request = call.request
        var awardsID: Int = request.queryParameters["awardsID"]?.toInt() ?: 0 //参评id
        val pub = getawardsPubById(awardsID)
        val awardSigns = JdbcConnection.bootstrap.queryTable(AwardSign::class.java)
            .addCondition { c -> c.add(C.eq("awardsID", awardsID)) }
            .list(AwardSign::class.java)

        awardSigns?.forEach { sig ->
            sig.awardsPub = pub
        }
        writeGsonResponds(JSON.toJSONString(HttpResult<List<AwardSign>>(awardSigns, 200, "")), call)
    }


    suspend fun listMySignById(call: ApplicationCall) {
        val request = call.request
        var uid: Int = request.queryParameters["uid"]?.toInt() ?: 0
        var page = request.queryParameters["page"]?.toInt() ?: 0

        val awardSigns = JdbcConnection.bootstrap.queryTable(AwardSign::class.java).addCondition { c ->
            c.add(C.eq("uid", uid))

        }.limit(page * pageSize, pageSize)
            .sort(Sorts.DESC, "awardSignID")
            .list(AwardSign::class.java)


        awardSigns?.forEach { sig ->
            val pub = getawardsPubById(sig.awardsPubId)
            sig.awardsPub = pub
        }
        writeGsonResponds(JSON.toJSONString(HttpResult<List<AwardSign>>(awardSigns, 200, "")), call)
    }


    suspend fun getAwardsSignById(call: ApplicationCall) {
        val request = call.request
        var awardSignID: Int = request.queryParameters["awardSignID"]?.toInt() ?: 0 //参评id
        writeGsonResponds(JSON.toJSONString(HttpResult<AwardSign?>(getAwardsSignById(awardSignID), 200, "")), call)
    }


    suspend fun getAwardsPubById(call: ApplicationCall) {
        val request = call.request
        var awardsID: Int = request.queryParameters["awardsID"]?.toInt() ?: 0
        writeGsonResponds(JSON.toJSONString(HttpResult<AwardsPub?>(getawardsPubById(awardsID), 200, "")), call)
    }


    suspend fun listAwardSignComment(call: ApplicationCall) {
        val request = call.request
        var awardSignID: Int = request.queryParameters["awardSignID"]?.toInt() ?: 0 //参评id

        val awardSignComments = JdbcConnection.bootstrap.queryTable(AwardSignComment::class.java)
            .addCondition { c -> c.add(C.eq("awardSignID", awardSignID)) }
            .list(AwardSignComment::class.java)

        writeGsonResponds(JSON.toJSONString(HttpResult<List<AwardSignComment>>(awardSignComments, 200, "")), call)

    }


    suspend fun awardsComment(call: ApplicationCall) {
        val request = call.receiveParameters()
        var uid: Int = request["uid"]?.toInt() ?: 0
        var awardSignID: Int = request["awardSignID"]?.toInt() ?: 0
        val commentType = request["commentType"]?.toInt() ?: 0  //1通过　２不通过
        var commentContent: String = request["commentContent"] ?: ""

        val sign = getAwardsSignById(awardSignID)
        if (sign == null) {
            writeError("参数错误", 400, call)
            return
        }
        sign.pass = commentType
        JdbcConnection.bootstrap.query(sign).update()

        val comment = AwardSignComment()
        comment.awardSignID = sign.awardSignID
        comment.commentUid = uid
        comment.commentContent = commentContent
        comment.commentType = commentType
        val cid = JdbcConnection.bootstrap.query(comment).insert()
        comment.id = cid
        writeGsonResponds(JSON.toJSONString(HttpResult<AwardSignComment>(comment, 200, "")), call)

        val msg =
            WebSocketMsg<AwardSignComment>(
                WebSocketMsg.WebSocktMsgLevelMsgAwardComment,
                comment,
                uid,
                sign.uid
            )
        mSocketSever.sendFramByUid(sign.uid, msg)

    }

    suspend fun awardsSign(call: ApplicationCall) {

        val request = call.receiveParameters()

        var awardsPubId: Int = request["awardsPubId"]?.toInt() ?: 0
        var uid: Int = request["uid"]?.toInt() ?: 0
        var word: String? = request["word"] ?: ""  //文档地址　上传对象存储返回得到url
        var pass: Int = 0

        val pub = getawardsPubById(awardsPubId)
        if (pub == null) {
            writeError("参数错误", 400, call)
            return
        }
        val awardSign = AwardSign()
        awardSign.awardsPubId = awardsPubId
        awardSign.uid = uid
        awardSign.word = word
        awardSign.pass = pass
        val id = JdbcConnection.bootstrap.query(awardSign).insert()
        awardSign.awardSignID = id
        awardSign.awardsPub = pub
        writeGsonResponds(JSON.toJSONString(HttpResult<AwardSign>(awardSign, 200, "")), call)

        val msg =
            WebSocketMsg<AwardSign>(
                WebSocketMsg.WebSocktMsgLevelMsgAwardSign,
                awardSign,
                uid,
                pub.releaseID
            )
        mSocketSever.sendFramByUid(pub.releaseID, msg)
    }


    suspend fun pubAwards(call: ApplicationCall) {
        val request = call.receiveParameters()
        val releaseID: Int = request["leaveID"]?.toInt() ?: 0
        val awardsTitle: String = request["leaveID"] ?: ""
        val awardsContent: String = request["leaveID"] ?: ""
        val word: String = request["leaveID"] ?: ""   //文档地址　上传对象存储返回得到url
        val startTime: Long = request["leaveID"]?.toLong() ?: 0
        val endTime: Long = request["leaveID"]?.toLong() ?: 0
        val toClass: String = request["toClass"] ?: "" //接受班级　"1,2,4,6"

        val recs = toClass.split(",").toList()
        val awards = AwardsPub()
        awards.awardsID = releaseID
        awards.awardsTitle = awardsTitle
        awards.awardsContent = awardsContent
        awards.word = word
        awards.startTime = startTime
        awards.endTime = endTime
        awards.toClass = recs
        val id = JdbcConnection.bootstrap.query(awards).insert()
        awards.awardsID = id
        writeGsonResponds(JSON.toJSONString(HttpResult<AwardsPub>(awards, 200, "")), call)

        recs.forEach {
            val receive = AwardClass()
            receive.awardsID = id
            receive.classID = it.toInt()
            val receiveId = JdbcConnection.bootstrap.query(receive).insert()
            receive.awardsRecID = receiveId
            val users = userDao.getStudentByClassId(receive.classID)
            users?.forEach { user ->
                val msg =
                    WebSocketMsg<AwardsPub>(
                        WebSocketMsg.WebSocktMsgLevelMsgPubAward,
                        awards,
                        releaseID,
                        user.id
                    )

                mSocketSever.sendFramByUid(user.id, msg)
            }
        }

    }
}