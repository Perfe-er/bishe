package com.hapi

import api.AnnoDao
import api.ClassDao
import api.UserDao
import com.hapi.api.LeaveDao
import com.hapi.api.SignDao
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
import java.io.File
import java.util.*
import kotlin.coroutines.suspendCoroutine

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val mSocketSever = SocketSever()
val userDao by lazy { UserDao() }
val classDao by lazy { ClassDao() }
val annoDao by lazy { AnnoDao() }
val leaveDao by lazy { LeaveDao() }
val signDao by lazy { SignDao() }


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
                    while (true) {
                        val frame = incoming.receive()
//                        if (frame is Frame.Text) {
//                            send(Frame.Text("{$uid}Client said: " + frame.readText()))
//                        }
                        mSocketSever.onRecev(client, frame)
                    }
                }
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
            suspendCoroutine<Any> {
                userDao.getUserInfoById(call, it)
            }
        }


        post("/register") {
            suspendCoroutine<Any> {
                userDao.register(call, it)
            }
        }



        post("/login") {
            suspendCoroutine<Any> {
                userDao.login(call, it)
            }
        }


        //修改资料
        authenticate {
            route("/infoEdit") {
                post {
                    suspendCoroutine<Any> {
                        userDao.infoEdit(call, it)
                    }
                }
            }
        }

        //修改密码
        authenticate {
            route("/modifyPassWd") {
                post {
                    suspendCoroutine<Any> {
                        userDao.modifyPassWd(call, it)
                    }
                }
            }
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
                    suspendCoroutine<Any> {
                        classDao.createClass(call, it)
                    }
                }
            }
        }

        //删除班级
        authenticate {
            route("/deleteClass") {
                post {
                    suspendCoroutine<Any> {
                        classDao.deleteClass(call, it)
                    }
                }
            }
        }

        /**
         * 修改班级
         */
        authenticate {
            route("/modifyClass") {
                post {
                    suspendCoroutine<Any> {
                        classDao.modifyClass(call, it)
                    }
                }
            }
        }

        /**
         * 根据班名查询班级详情
         */
        get("/findClassByClassName") {
            suspendCoroutine<Any> {
                classDao.findClassByClassName(call, it)
            }
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
