package api

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
        var awardsID: Int = request.queryParameters["awardsID"]?.toInt() ?: 0 //评选id
        val pub = getawardsPubById(awardsID)
        val awardSigns = JdbcConnection.bootstrap.queryTable(AwardSign::class.java)
            .addCondition { c -> c.add(C.eq("awardsPubId", awardsID)) }
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


    suspend fun editPass(call: ApplicationCall){
        val request = call.receiveParameters()
        val awardSignID : Int = request["awardSignID"]?.toInt()?:0
        val pass : Int = 1
        val awardSign = AwardSign()
        awardSign.awardSignID = awardSignID
        awardSign.pass = pass
        JdbcConnection.bootstrap.query(awardSign).setFields("pass").update();
        writeGsonResponds(JSON.toJSONString(HttpResult(awardSign,200,"通过")),call)
    }


    suspend fun awardsComment(call: ApplicationCall) {
        val request = call.receiveParameters()
        var uid: Int = request["uid"]?.toInt() ?: 0
        var awardSignID: Int = request["awardSignID"]?.toInt() ?: 0
        val commentType = 2  //1通过　２不通过
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
        writeGsonResponds(JSON.toJSONString(HttpResult<AwardSignComment>(comment, 200, "建议发送成功")), call)

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
        var date: Long = request["date"]?.toLong() ?: 0
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
        awardSign.date = date
        val id = JdbcConnection.bootstrap.query(awardSign).insert()
        awardSign.awardSignID = id
        awardSign.awardsPub = pub
        writeGsonResponds(JSON.toJSONString(HttpResult<AwardSign>(awardSign, 200, "报名成功")), call)

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
        val releaseID: Int = request["releaseID"]?.toInt() ?: 0
        val awardsTitle: String = request["awardsTitle"] ?: ""
        val awardsContent: String = request["awardsContent"] ?: ""
        val word: String = request["word"] ?: ""   //文档地址　上传对象存储返回得到url
        val startTime: Long = request["startTime"]?.toLong() ?: 0
        val endTime: Long = request["endTime"]?.toLong() ?: 0
        val toClass: String = request["toClass"] ?: "" //接受班级　"1,2,4,6"

        val recs = toClass.split(",").toList()
        val awards = AwardsPub()
        awards.releaseID = releaseID
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

    suspend fun getListAwards(call: ApplicationCall){
        val request = call.request
        val classID = request.queryParameters["classID"]?.toInt()
        val page = request.queryParameters["page"]?.toInt() ?: 0
        val awardsRes = ArrayList<AwardsPub>()
        val awardsClass =
            JdbcConnection.bootstrap.queryTable(AwardClass::class.java).limit(
                page * pageSize,
                pageSize
            )
                .sort(Sorts.DESC, "awardsRecID")
                .addCondition { c -> c.add(C.eq("classID", classID)) }
                .list(AwardClass::class.java)
        awardsClass?.forEach{
            val awards =
                JdbcConnection.bootstrap.queryTable(AwardsPub::class.java)
                    .addCondition { c -> c.add(C.eq("awardsID", it.awardsID)) }
                    .list(AwardsPub::class.java)
            awardsRes.addAll(awards)
        }
        writeGsonResponds(JSON.toJSONString(HttpResult(awardsRes, 200, "成功")), call)
    }

    suspend fun deleteAwards(call: ApplicationCall) {

        val request = call.receiveParameters()
        val awardsID = request["awardsID"]?.toInt()
        val award = AwardsPub()
        award.awardsID = awardsID ?: 0
        JdbcConnection.bootstrap.query(award).delete()

        writeGsonResponds(JSON.toJSONString(HttpResult(award, 200, "删除")), call)
    }

    suspend fun modifyAwards(call: ApplicationCall) {

        val request = call.receiveParameters()
        val awardsID = request["awardsID"]?.toInt()
        val awardsTitle = request["awardsTitle"]//className
        val awardsContent = request["awardsContent"]//className
        val releaseID = Integer.valueOf(request["releaseID"])
        val word = request["word"]
        val startTime = request["startTime"]
        val endTime = request["endTime"]
        val startTime1:Long = startTime?.toLong()?:0
        val endTime1:Long = endTime?.toLong()?:0
        val awards = AwardsPub()
        awards.awardsID = awardsID ?: 0
        awards.awardsTitle = awardsTitle
        awards.awardsContent = awardsContent
        awards.releaseID = releaseID
        awards.word = word
        awards.startTime = startTime1
        awards.endTime = endTime1
        try {
            JdbcConnection.bootstrap.query(awards).setFields("awardsTitle", "awardsContent","releaseID","word","startTime","endTime").update()
            writeGsonResponds(JSON.toJSONString(HttpResult(awards, 200, "更新成功")), call)
        } catch (e: Exception) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "更新失败${e.message}")), call)
        }


    }

    /**
     * 搜索
     */
    suspend fun searchAwards(call: ApplicationCall) {

        val request = call.request
        val keyWorlds = request.queryParameters["keyWorlds"]
        val awardsRes = java.util.ArrayList<AwardsPub>()
        if (!TextUtils.isEmpty(keyWorlds)) {
            val awards =
                JdbcConnection.bootstrap.queryTable(AwardsPub::class.java)

                    .addCondition { c -> c.add(C.contains("awardsTitle", keyWorlds)) }
                    .limit(0, pageSize)
                    .list(AwardsPub::class.java)
            awardsRes.addAll(awards)
        }
        writeGsonResponds(JSON.toJSONString(HttpResult(awardsRes, 200, "成功")), call)

    }
}