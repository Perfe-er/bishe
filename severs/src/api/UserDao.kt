package api

import been.HttpResult
import been.User
import com.alibaba.fastjson.JSON
import com.example.classDao
import com.example.userDao
import db.JdbcConnection
import io.ktor.application.ApplicationCall
import io.ktor.client.request.request
import io.ktor.request.receiveParameters
import online.sanen.cdm.api.condition.C
import org.apache.http.util.TextUtils
import org.sqlite.jdbc3.JDBC3Connection
import websocket.Auth

class UserDao : BaseDao() {

    fun updateNumber(number: Double,id: Int):User? {
        val users = JdbcConnection.bootstrap.queryTable(User::class.java).addCondition { c -> c.add(C.eq("id", id)) }
            .list(User::class.java)
        if (users.isEmpty()) {
            null
        } else {
            val user = User()
            user.id = id
            user.number = number
            JdbcConnection.bootstrap.query(user).setFields("number").update();
        }
        return null
    }

    fun getUserById(id: Int): User? {
        val users = JdbcConnection.bootstrap.queryTable(User::class.java).addCondition { c -> c.add(C.eq("id", id)) }
            .list(User::class.java)
        return if (users.isEmpty()) {
            null
        } else {
            users[0]
        }
    }


    fun getStudentByClassId(classID: Int): List<User> {

        val users =
            JdbcConnection.bootstrap.queryTable(User::class.java).addCondition { c -> c.add(C.eq("classID", classID)) }
                .list(User::class.java)
        println("getStudentByClassId" + classID + " " + users.size)
        return users
    }

    suspend fun getAssistantByClassID(call: ApplicationCall){
        val request = call.request
        val classID = request.queryParameters["classID"]
        val classID1 = Integer.valueOf(classID)
        val founder = classDao.getClassByClassId(classID1)
        if (founder != null){
            val id = founder.founderID
            val user = getUserById(id)
            writeGsonResponds(JSON.toJSONString(HttpResult(user,200,"")),call)
        }
    }

    suspend fun getClassmate(call: ApplicationCall){
        val request = call.request
        val classID = request.queryParameters["classID"]
        val classID1 = Integer.valueOf(classID)
        val users = getStudentByClassId(classID1)
        writeGsonResponds(JSON.toJSONString(HttpResult<List<User>>(users,200,"")),call)
    }


    suspend  fun getUserInfoById(call: ApplicationCall) {
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
        val parentPho = request["parentPho"]
        val identity = request["identity"]
        val address = request["address"]
        val birthday = request["birthday"]

        val id1 = Integer.valueOf(id)
        val sex1 = Integer.valueOf(sex)
        val user = User()

        user.id = id1
        user.stuID = stuID
        user.name = name
        user.sex = sex1
        user.college = college
        user.parentPho = parentPho
        user.identity = identity
        user.address = address
        user.birthday = birthday

        JdbcConnection.bootstrap.query(user).setFields(
            "college",
            "parentPho",
            "identity",
            "address",
            "birthday",
            "stuID",
            "name",
            "sex"
        ).update()
        writeGsonResponds(JSON.toJSONString(HttpResult(user, 200, "修改成功")), call)

    }

    suspend fun editHead(call: ApplicationCall){
        val request = call.receiveParameters()
        val id:Int = request["id"]?.toInt()?:0
        val head = request["head"]
        val user =  User()
        user.id = id
        user.head = head
        JdbcConnection.bootstrap.query(user).setFields("head").update()
        writeGsonResponds(JSON.toJSONString(HttpResult(user,200,"更换成功")),call)
    }

    suspend fun editClass(call: ApplicationCall){
        val request = call.receiveParameters()
        val id = request["id"]
        val className = request["className"]
        val classID = request["classID"]
        val classID1 = Integer.valueOf(classID)
        val id1 = Integer.valueOf(id)
        val user = User()
        user.className = className
        user.classID = classID1
        user.id = id1
        JdbcConnection.bootstrap.query(user).setFields("className","classID").update()
        writeGsonResponds(JSON.toJSONString(HttpResult(user,200,"加入成功")),call)
    }
    /**
     * 切换身份
     */
    suspend fun editStuType(call: ApplicationCall){
        val request = call.receiveParameters()
        val id = request["id"]
        val stuType = request["stuType"]
        val stuType1 = Integer.valueOf(stuType)
        val id1 = Integer.valueOf(id)
        val user = User()
        user.id = id1
        user.stuType = stuType1
        JdbcConnection.bootstrap.query(user).setFields("stuType").update()
        writeGsonResponds(JSON.toJSONString(HttpResult(user,200,"切换成功")),call)
    }

    //忘记密码
    suspend fun modifyPassWdByPhone(call: ApplicationCall){
        val request =  call.receiveParameters()
        val id = request["id"]
        val phone = request["phone"]
        val passWd = request["passWd"]
        val user = getUserById(Integer.parseInt(id!!))
        val users = JdbcConnection.bootstrap.queryTable(User::class.java).addCondition { c ->
            c.add(C.eq("phone", phone))
        }.list(User::class.java)

        if (users.isEmpty()){
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400,"该账号还未被注册")),call)
            return
        }
        if (user == null) {
            writeGsonResponds(JSON.toJSONString(HttpResult<Unit>(400, "用户id错误")), call)
            return
        }
            user.passWd = passWd
            JdbcConnection.bootstrap.query(user).setFields("passWd").update()
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


