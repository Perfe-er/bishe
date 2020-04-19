package com.example.api

import api.BaseDao
import api.UserDao
import been.AnnoReceive
import been.HttpResult
import been.Moral
import been.MoralReceive
import com.alibaba.fastjson.JSON
import com.example.userDao
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
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
        for (i in 0 until len - 1) {
            val cid = Integer.parseInt(recs[i])
            val receive = MoralReceive()
            val user = userDao.getUserById(i)
            val number1 = user!!.number
            val number = number1 - fine + add
            receive.moralID = moralID1
            receive.classID = user.classID
            receive.number = number
            receive.stuID = cid

            receives.add(receive)
        }
        JdbcConnection.bootstrap.query(receives).insert()
        writeGsonResponds(JSON.toJSONString(HttpResult<Moral>(moral,200,"")),call)

    }

}