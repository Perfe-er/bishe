package api

import been.*
import com.alibaba.fastjson.JSON
import com.hapi.mSocketSever
import com.hapi.pageSize
import com.hapi.userDao
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import online.sanen.cdm.api.basic.Sorts
import online.sanen.cdm.api.condition.C
import org.apache.http.util.TextUtils

import java.util.ArrayList


class AnnoDao : BaseDao() {


    /**
     * 搜索
     */
    suspend fun searchAnno(call: ApplicationCall) {

        val request = call.request
        val keyWorlds = request.queryParameters["keyWorlds"]
        val annoRes = ArrayList<Anno>()
        if (!TextUtils.isEmpty(keyWorlds)) {
            val annos =
                JdbcConnection.bootstrap.queryTable(Anno::class.java)

                    .addCondition { c -> c.add(C.contains("annoTitle", keyWorlds)) }
                    .limit(0, pageSize)
                    .list(Anno::class.java)
            annoRes.addAll(annos)
        }
        writeGsonResponds(JSON.toJSONString(HttpResult(annoRes, 200, "成功")), call)

    }

    /**
     * 公告列表
     */
    suspend fun listAnno(call: ApplicationCall) {

        val request = call.request
        val classID = request.queryParameters["classID"]?.toInt()
        val page = request.queryParameters["page"]?.toInt() ?: 0
        val annoRes = ArrayList<Anno>();
        val annoReceives =
            JdbcConnection.bootstrap.queryTable(AnnoReceive::class.java).limit(
                page * pageSize,
                pageSize
            )
                .sort(Sorts.DESC, "id")
                .addCondition { c -> c.add(C.eq("classID", classID)) }
                .list(AnnoReceive::class.java)
        annoReceives?.forEach {
            val annos =
                JdbcConnection.bootstrap.queryTable(Anno::class.java)
                    .addCondition { c -> c.add(C.eq("annoID", it.annoID)) }
                    .list(Anno::class.java)
            annoRes.addAll(annos)
        }

        writeGsonResponds(JSON.toJSONString(HttpResult(annoRes, 200, "成功")), call)

    }

    /**
     * 发布公告
     * 接收班级id classIDs  ->  "1,2,3,4,7" 中间用,分割
     */

    suspend fun pubAnno(call: ApplicationCall) {


        val request = call.receiveParameters()
        val annoTitle = request["annoTitle"]//className
        val content = request["content"]//className
        val releaseID = request["releaseID"]//className
        val releDate = request["releDate"]//className
        val classIDs = request["classIDs"]//接收班级id classIDs  ->  "1,2,3,4,7" 中间用,分割
        val anno = Anno()

        if (TextUtils.isEmpty(annoTitle)
            || TextUtils.isEmpty(content)
            || TextUtils.isEmpty(releaseID)
            || TextUtils.isEmpty(releDate)
            || TextUtils.isEmpty(classIDs)
        ) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "参数有误")), call)
        } else {

            anno.annoTitle = annoTitle
            anno.content = content
            anno.releaseID = Integer.parseInt(releaseID!!)
            anno.releDate = java.lang.Long.parseLong(releDate!!)
            val id = JdbcConnection.bootstrap.query(anno).insert()
            anno.annoID = id
            val receives = ArrayList<AnnoReceive>()
            val recs = classIDs!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val len = recs.size

            anno.classIds = recs.asList()
            for (i in 0 until len - 1) {
                val cid = Integer.parseInt(recs[i])
                val receive = AnnoReceive()
                receive.annoID = id
                receive.classID = cid
                receives.add(receive)

                GlobalScope.launch(Dispatchers.IO) {
                    userDao.getStudentByClassId(cid)?.forEach {
                        val annoMsg =
                            WebSocketMsg<Anno>(
                                WebSocketMsg.WebSocktMsgAnnoMsg,
                                anno,
                                releaseID.toInt(),
                                it.id

                        )

                        mSocketSever.sendFramByUid(it.id, annoMsg)
                    }
                }
            }

            JdbcConnection.bootstrap.query(receives).insert()
            writeGsonResponds(JSON.toJSONString(HttpResult(anno, 200, "发布成功")), call)
        }

    }


    suspend fun deleteAnno(call: ApplicationCall) {

        val request = call.receiveParameters()
        val annoID = request["annoID"]?.toInt()
        val anno = Anno()
        anno.annoID = annoID ?: 0
        JdbcConnection.bootstrap.query(anno).delete()

        writeGsonResponds(JSON.toJSONString(HttpResult(anno, 200, "删除")), call)
    }


    suspend fun modifyAnno(call: ApplicationCall) {

        val request = call.receiveParameters()
        val annoID = request["annoID"]?.toInt()
        val annoTitle = request["annoTitle"]//className
        val content = request["content"]//className
        val anno = Anno()
        anno.annoID = annoID ?: 0
        anno.annoTitle = annoTitle
        anno.content = content
        try {
            JdbcConnection.bootstrap.query(anno).setFields("annoTitle", "content").update()
            writeGsonResponds(JSON.toJSONString(HttpResult(anno, 200, "跟新成功")), call)
        } catch (e: Exception) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "跟新失败${e.message}")), call)
        }


    }


}
