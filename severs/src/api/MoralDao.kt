package com.example.api

import api.BaseDao
import api.UserDao
import been.*
import com.alibaba.fastjson.JSON
import com.example.pageSize
import com.example.userDao
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import io.ktor.client.request.request
import io.ktor.request.receiveParameters
import online.sanen.cdm.api.basic.Sorts
import online.sanen.cdm.api.condition.C
import java.util.ArrayList

class MoralDao : BaseDao() {

    suspend fun createMoral(call: ApplicationCall){
        val request = call.receiveParameters()
        val ids = request["ids"]
        val changeP: Int = request["changeP"]?.toInt() ?: 0
        val reason= request["reason"]
        val fine: Double = request["fine"]?.toDouble() ?:0.00
        val add:Double = request["add"]?.toDouble() ?:0.00
        val dateTime: Long = request["dateTime"]?.toLong()?:0
        val moral = Moral()
        moral.changeP = changeP
        moral.reason = reason
        moral.fine = fine
        moral.add = add
        moral.dateTime = dateTime
        val moralID1 = JdbcConnection.bootstrap.query(moral).insert()
        moral.moralID = moralID1

        val receives = ArrayList<MoralReceive>()
        val recs = ids!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val len = recs.size
        moral.ids =  recs.asList()
        for (i in 0 until len) {
            val cid = Integer.parseInt(recs[i])
            val receive = MoralReceive()
            val user = userDao.getUserById(cid)
            val number1 = user!!.number
            val number = number1 + add - fine
            userDao.updateNumber(number,cid)
            receive.moralID = moralID1
            receive.classID = user.classID
            receive.number = number
            receive.stuID = cid

            receives.add(receive)
        }
        JdbcConnection.bootstrap.query(receives).insert()
        writeGsonResponds(JSON.toJSONString(HttpResult<Moral>(moral,200,"")),call)

    }
    
    suspend fun moralRecord(call: ApplicationCall){
        val request = call.request
        val stuID :Int = request.queryParameters["stuID"]?.toInt()?:0
        val moralRes = ArrayList<Moral>()
        val moralReceives =
            JdbcConnection.bootstrap.queryTable(MoralReceive::class.java)
                .sort(Sorts.DESC, "moralReceiveID")
                .addCondition { c -> c.add(C.eq("stuID", stuID)) }
                .list(MoralReceive::class.java)
        moralReceives?.forEach {
            val morals =
                JdbcConnection.bootstrap.queryTable(Moral::class.java)
                    .addCondition { c -> c.add(C.eq("moralID", it.moralID)) }
                    .list(Moral::class.java)
            moralRes.addAll(morals)
        }
        writeGsonResponds(JSON.toJSONString(HttpResult(moralRes,200,"")),call)
    }

}