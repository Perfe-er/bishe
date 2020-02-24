package com.blabla

import api.UserDao
import been.User
import com.alibaba.fastjson.JSON
import com.blabla.api.userRoute
import com.blabla.db.JdbcConnection
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.gson.*
import io.ktor.features.*
import com.fasterxml.jackson.databind.*
import com.mhdt.structure.LinkedCaseInsensitiveMap
import io.ktor.jackson.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.ContentType.Application.Json
import sun.rmi.runtime.Log
import kotlin.coroutines.suspendCoroutine

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    JdbcConnection.initConnection()
    install(ContentNegotiation) {
        gson {
        }

        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val client = HttpClient(Apache) {
    }

    routing {

     //   userRoute(this)

        get("/") {

            val list = JdbcConnection.bootstrap.queryTable<User>(User::class.java).list(User::class.java)
            var s=""
            list.forEach{
                s+it.name
                s+"  "+it.uid
            }
            call.respondText(JSON.toJSONString(list), Json)
        }


        get("/listUser") {
            suspendCoroutine<Any> {
                UserDao().listUser(call,it)
            }
        }



        post("/login") {
            suspendCoroutine<Any> {
                UserDao().login(call,it)
            }
        }


        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
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

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
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
