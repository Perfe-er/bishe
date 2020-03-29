package api

import been.HttpResult
import been.User
import com.alibaba.fastjson.JSON
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import online.sanen.cdm.api.condition.C
import org.apache.http.util.TextUtils
import websocket.Auth

class UserDao : BaseDao() {


    fun getUserById(id: Int): User? {
        val users = JdbcConnection.bootstrap.queryTable(User::class.java).addCondition { c -> c.add(C.eq("id", id)) }
            .list(User::class.java)
        return if (users.isEmpty()) {
            null
        } else {
            users[0]
        }
    }


    fun getStudentByClassId(calssId: Int): List<User> {

        val users =
            JdbcConnection.bootstrap.queryTable(User::class.java).addCondition { c -> c.add(C.eq("classID", calssId)) }
                .list(User::class.java)
        println("getStudentByClassId" + calssId + " " + users.size)
        return users
    }


    suspend   fun getUserInfoById(call: ApplicationCall) {
        val request = call.request
        val id = request.queryParameters["id"]
        if (TextUtils.isEmpty(id)) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "ｉｄ need")), call)
        }
        val u = getUserById(Integer.parseInt(id!!))
        u!!.passWd = ""
        writeGsonResponds(JSON.toJSONString(HttpResult(u, 200, "注册成功")), call)
    }


    //注册
    suspend fun register(call: ApplicationCall) {
        val request = call.receiveParameters()
        val phone = request["phone"]
        val passWd = request["passWd"]
        val users =
            JdbcConnection.bootstrap.queryTable(User::class.java).addCondition { c -> c.add(C.eq("phone", phone)) }
                .list(User::class.java)
        if (users.isEmpty()) {
            val user = User()
            user.phone = phone
            user.passWd = passWd
            val user1 = JdbcConnection.bootstrap.query(user).insert()
            user.id = user1
            val t = Auth.sign(user.id.toString() + user.name)["token"]
            user.token = t
            writeGsonResponds(JSON.toJSONString(HttpResult(user, 200, "注册成功")), call)

        } else {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "用户已存在")), call)
        }
    }

    //登录
    suspend  fun login(call: ApplicationCall) {
        val request = call.receiveParameters()
        //客户端参数
        val phone = request["phone"]
        //客户端参数
        val passWd = request["passWd"]

        println("login  \${phone} \${passWd}$phone $passWd")

        val users = JdbcConnection.bootstrap.queryTable(User::class.java).addCondition { c ->
            c.add(C.eq("phone", phone))
            c.add(C.eq("passWd", passWd))
        }.list(User::class.java)

        if (users.isEmpty()) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "用户名或密码错误")), call)
        } else {
            val u = users[0]
            val t = Auth.sign(u.id.toString() + u.name)["token"]
            u.token = t
            writeGsonResponds(JSON.toJSONString(HttpResult(u, 200, "登录成功")), call)
        }
    }

    suspend fun infoEdit(call: ApplicationCall) {
        //请求
        val request = call.receiveParameters()
        val id = request["id"]
        val stuID = request["stuID"]
        val name = request["name"]
        val sex = request["sex"]
        val college = request["college"]
//        val className = request["className"]
//        val classID = request["classID"]
//        val number = request["number"]
        val parentPho = request["parentPho"]
        val identity = request["identity"]
        val address = request["address"]
        val birthday = request["birthday"]
//        val stuType = request["stuType"]

        val id1 = Integer.valueOf(id)
        val sex1 = Integer.valueOf(sex)
//        val classID1 = Integer.valueOf(classID)
//        val number1 = Integer.valueOf(number)
//        val stuType1 = Integer.valueOf(stuType)
        val user = User()

        user.id = id1
        user.stuID = stuID
        user.name = name
        user.sex = sex1
        user.college = college
//        user.className = className
//        user.classID = classID1
//        user.number = number1
        user.parentPho = parentPho
        user.identity = identity
        user.address = address
        user.birthday = birthday
//        user.stuType = stuType1

        JdbcConnection.bootstrap.query(user).setFields(
            "college",
//            "className",
//            "classID",
//            "number",
            "parentPho",
            "identity",
            "address",
            "birthday",
//            "stuType",
            "stuID",
            "name",
            "sex"
        ).update()
        writeGsonResponds(JSON.toJSONString(HttpResult(user, 200, "修改成功")), call)

    }

    /**
     * 修改密码
     *
     * @param call
     * @param continuation
     */
   suspend fun modifyPassWd(call: ApplicationCall) {

        //请求
        val request = call.receiveParameters()
        val id = request["id"]
        val oldPwd = request["oldPwd"]
        val newPwd = request["newPwd"]

        val user = getUserById(Integer.parseInt(id!!))
        if (user == null) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "用户id错误")), call)
            return
        }
        if (user.passWd != oldPwd) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "旧密码错误")), call)
            return
        }
        user.passWd = newPwd
        JdbcConnection.bootstrap.query(user).setFields("passWd").update()
        writeGsonResponds(JSON.toJSONString(HttpResult(user, 200, "修改成功")), call)

    }


}
