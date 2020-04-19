package com.example

import api.*
import com.example.api.ActivityDao
import com.example.api.MoralDao
import com.example.api.SignDao
import db.JdbcConnection
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import java.time.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import org.apache.http.auth.InvalidCredentialsException
import org.apache.http.util.TextUtils
import websocket.Auth
import websocket.KtSession
import websocket.SocketSever
import websocket.SocktSessionClient
import java.io.File
import java.lang.Exception

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val mSocketSever = SocketSever()
val userDao by lazy { UserDao() }
val classDao by lazy { ClassDao() }
val annoDao by lazy { AnnoDao() }
val leaveDao by lazy { LeaveDao() }
val signDao by lazy { SignDao() }
val awardsDao by lazy { AwardsDao() }
val moralDao by lazy { MoralDao() }
val activityDao by lazy { ActivityDao() }

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    JdbcConnection.initConnection()
    val verifier = Auth.makeJwtVerifier()
    install(Authentication) {
        jwt {
            verifier(verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }
    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
        }
    }

    install(Sessions) {
        cookie<KtSession>("MySession", directorySessionStorage(File(".sessions"))) {
            cookie.path = "/"
        }
    }

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }


    install(ContentNegotiation) {
        gson {
        }
    }

    val client = HttpClient(Apache) {
    }



    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/session") {
            val s = call.sessions.get("MySession") as? KtSession
            if (s == null) {
                call.sessions.set("MySession", KtSession(111))
                call.respondText { "generated new session" }
            } else {
                call.respondText { "name: ${s.uid}" }
            }
        }

        get("tokenTest") {
            val t = Auth.sign("满家乐")["token"]
            call.respondText("HELLO WORLD! ${t}", contentType = ContentType.Text.Plain)

        }


            get("/html-dsl") {
                call.respondHtml {

                    body {
                        h1 { +"HTML" }
                        ul {
                            for (n in 1..10) {
                                li { +"$i" }
                            }
                        }
                    }
                }
            }

            authenticate {
                route("/secret") {
                    get {
                        val user = call.authentication.principal<UserIdPrincipal>() ?: "未认证"
                        call.respondText("hi ${user}, you are authenticated.", contentType = ContentType.Text.Plain)
                    }
                }
            }

            get("/styles.css") {
                call.respondCss {
                    body {
                        backgroundColor = Color.red
                    }
                    p {
                        fontSize = 2.em
                    }
                    rule("p.myclass") {
                        color = Color.blue
                    }
                }
            }


            /**
             * webSocket
             */


            webSocket("/myws/echo") {


                val uid = this.call.parameters["uid"]
                System.out.println("收到连接 ${uid}")
                if (TextUtils.isEmpty(uid)) {
                    this.close()
                    return@webSocket
                }
                send(Frame.Text("Hi from server"))
                val client = SocktSessionClient(Integer.parseInt(uid), "", this)
                mSocketSever.clients += client
                mSocketSever.incoming(client)
                try {
                    while (true) {

                        val frame = incoming.receive()
//                        if (frame is Frame.Text) {
//                            send(Frame.Text("{$uid}Client said: " + frame.readText()))
//                        }
                        mSocketSever.onRecev(client, frame)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    mSocketSever.clients -= client
                }

            }


            /***
             *
             *
             * 用户相关
             *
             *
             *
             */
            get("/json/gson") {
                call.respond(mapOf("hello" to "world"))
            }


            /**
             * 根据用户id查看用户资料
             */
            get("/getUserInfoById") {

                userDao.getUserInfoById(call)
            }

            get("/getClassmate"){
                userDao.getClassmate(call)
            }

            get("/getAssistantByClassID"){
                userDao.getAssistantByClassID(call)
            }

            post("/register") {
                userDao.register(call)
            }


            post("/login") {
                userDao.login(call)
            }


            //修改资料
            authenticate {
                route("/infoEdit") {
                    post {
                        userDao.infoEdit(call)
                    }
                }
            }

            authenticate {
                route("/editStuType"){
                    post {
                        userDao.editStuType(call)
                    }
                }
            }

            authenticate {
                route("/editClass"){
                    post{
                        userDao.editClass(call)
                    }
                }
            }
            //修改密码
            authenticate {
                route("/modifyPassWd") {
                    post {
                        userDao.modifyPassWd(call)
                    }
                }
            }

            //通过手机号
            post("/modifyPassWdByPhone"){
                userDao.modifyPassWdByPhone(call)
            }


            /***
             *
             *
             * 班级相关
             *
             *
             *
             */

            //创建班级
            authenticate {
                route("/createClass") {
                    post {
                        classDao.createClass(call)
                    }
                }
            }

            //删除班级
            authenticate {
                route("/deleteClass") {
                    post {
                        classDao.deleteClass(call)
                    }
                }
            }

            /**
             * 修改班级
             */
            authenticate {
                route("/modifyClass") {
                    post {
                        classDao.modifyClass(call)
                    }
                }
            }

            /**
             * 根据班名查询班级详情
             */
            get("/findClassByClassName") {
                classDao.findClassByClassName(call)
            }


            get("/showClassByFounder"){
                classDao.showClassByFounder(call)
            }


            /***
             *
             *
             * 公告相关
             *
             *
             *
             */

            /**
             * 发布
             */
            authenticate {
                route("/pubAnno") {
                    post {
                        annoDao.pubAnno(call)
                    }
                }
            }

            /**
             * 公告列表
             */
            get("/listAnno") {
                annoDao.listAnno(call)
            }

            /**
             * 搜索公告
             */
            get("/searchAnno") {
                annoDao.searchAnno(call)
            }


            /**
             * 删除公告
             */
            authenticate {
                route("/deleteAnno") {
                    post {
                        annoDao.deleteAnno(call)
                    }
                }
            }

            /**
             * 跟新公告
             */
            authenticate {
                route("/modifyAnno") {
                    post {
                        annoDao.modifyAnno(call)
                    }
                }
            }


            /**
             * 请假
             *
             *
             *
             *
             *
             *
             *
             *
             */


            /**
             * 根据请假id获取刷新请假详情
             */
            get("/leveDetails") {
                leaveDao.leveDetails(call)
            }

            /**
             * 根据学生id 查询学习请假记录
             */
            get("/listlLeve") {
                leaveDao.listlLeve(call)
            }

            /**
             * 导员查看所有成员请假记录
             */
            get("/listLeaveByRatifyID"){
                    leaveDao.listLeaveByRatifyID(call)
                }

            /**
             * 班委查看成员请假记录
             */
            get("/listLeaveByClassID"){
                leaveDao.listLeaveByClassID(call)
            }
            /**
             * 发请假
             */
            authenticate {
                route("/createLeave") {
                    post {
                        leaveDao.createLeave(call)
                    }
                }
            }
            /**
             * 删除请假
             */
            authenticate {
                route("/delLeave") {
                    post {
                        leaveDao.delLeave(call)
                    }
                }
            }
            /**
             * 审批请假
             */
            authenticate {
                route("/ratifyLeave") {
                    post {
                        leaveDao.ratifyLeave(call)
                    }
                }
            }


            /***
             *
             *
             *
             *
             * 签到
             *
             *
             */


            /**
             * 查询已经签到或者还没签到的人
             */
            get("/getSignedUser") {
                signDao.getSignedUser(call)
            }


            //根据签到id刷新获取签到信息
            get("/getSiginById") {
                signDao.getSiginById(call)
            }

            //获取某个用户应该操作或已经操作的签到
            get("/getSiginOfUserRecev") {
                signDao.getSiginOfUserRecev(call)
            }
            //获取某人发补过的签到
            get("/getSiginOfUserPub") {
                signDao.getSiginOfUserPub(call)
            }
            /**
             * 发布签到给班级
             */
            authenticate {
                route("/pubSign") {
                    post {
                        signDao.pubSign(call)
                    }
                }
            }
            //签个到
            authenticate {
                route("/sign") {
                    post {
                        signDao.sign(call)
                    }
                }
            }


            /**
             * 奖学金
             *
             *
             *
             *
             *
             *
             */

            /**
            * 奖学金列表
            */
            get("/getListAwards"){
                awardsDao.getListAwards(call)
            }
            /**
             * 获取导员发布的奖学金的所有参评
             */
            get("/getawardsSignOfPub") {
                awardsDao.getawardsSignOfPub(call)
            }
            /**
             * 我的参评
             */
            get("/listMySignById") {
                awardsDao.listMySignById(call)
            }
            /**
             * 根据参评id获取参评信息
             */
            get("/getAwardsSignById") {
                awardsDao.getAwardsSignById(call)
            }
            /**
             *  获取导员发布的奖学金id　获取发布信息
             */
            get("/getAwardsPubById") {
                awardsDao.getAwardsPubById(call)
            }
            /**
             * 某条参评的评论列表
             */
            get("/listAwardSignComment") {
                awardsDao.listAwardSignComment(call)
            }

            /**
             * 评论
             */
            authenticate {
                route("/awardsComment") {
                    post {
                        awardsDao.awardsComment(call)
                    }
                }
            }
            /**
             * 参评
             */
            authenticate {
                route("/awardsSign") {
                    post {
                        awardsDao.awardsSign(call)
                    }
                }
            }
            /**
             * 发布奖学金信息
             */
            authenticate {
                route("/pubAwards") {
                    post {
                        awardsDao.pubAwards(call)
                    }
                }
            }

            authenticate{
                route("/deleteAwards"){
                    post {
                        awardsDao.deleteAwards(call)
                    }
                }
            }

            authenticate {
                route("/modifyAwards"){
                    post{
                        awardsDao.modifyAwards(call)
                    }
                }
            }

            get("/searchAwards"){
                awardsDao.searchAwards(call)
            }


            /**
             * 德育
             */
            authenticate {
                route("/createMoral"){
                    post {
                        moralDao.createMoral(call)
                    }
                }
            }

            /**
             * 活动
             */
            authenticate {
                route("/signActivity"){
                    post {
                        activityDao.signActivity(call)
                    }
                }
            }

            authenticate {
                route("/pubActivity"){
                    post {
                        activityDao.pubActivity(call)
                    }
                }
            }
            authenticate {
                route("/deleteActivity"){
                    post {
                        activityDao.deleteActivity(call)
                    }
                }
            }

            authenticate {
                route("/modifyActivity"){
                    post {
                        activityDao.modifyActivity(call)
                    }
                }
            }

            get("/searchActivity") {
                activityDao.searchActivity(call)
            }

            get("/listActivity") {
                activityDao.listActivity(call)
            }
    }
}
    fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
        style(type = ContentType.Text.CSS.toString()) {
            +CSSBuilder().apply(builder).toString()
        }
    }

    fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
        this.style = CSSBuilder().apply(builder).toString().trim()
    }

    suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
        this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
    }

