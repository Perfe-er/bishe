package websocket

import been.*
import com.alibaba.fastjson.JSON
import db.JdbcConnection
import java.util.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import online.sanen.cdm.api.condition.C

class SocketSever {

    val clients = Collections.synchronizedSet(LinkedHashSet<SocktSessionClient>())

    /**
     * 收sock消息
     */

    suspend fun onRecev(client: SocktSessionClient, frame: Frame) {
        if (frame is Frame.Text) {
            val text = frame.readText()
            if(text=="ping"){
                client.session.send("ping")
            }
        }

    }

    suspend fun incoming(client: SocktSessionClient) {

        val msgs =
            JdbcConnection.bootstrap.queryTable(WebSocketMsgRecod::class.java)
                .addCondition { c -> c.add(C.eq("uid", client.uid.toString())) }
                .list(WebSocketMsgRecod::class.java)

        msgs.forEach {
            if (System.currentTimeMillis() - it.time < 7 * 24 * 60 * 60 * 1000) {
                client.session.send(it.msg)
            }
            JdbcConnection.bootstrap.query(it).delete()
        }

    }


    /**
     * 发送sock消息
     */
    suspend fun <T> sendFramByUid(uid: Int, webSocketMsg: WebSocketMsg<T>) {
        System.out.println(uid.toString() + "sendFramByUid")
        val text = JSON.toJSONString(webSocketMsg)
        var isOnline = false
        clients?.forEach {
            System.out.println(it.uid.toString() + clients)
            if (it.uid == uid) {
                isOnline = true
                it.session.send(text)
            }
        }
        if (!isOnline) {
            val recod = WebSocketMsgRecod()
            recod.msg = text
            recod.time = System.currentTimeMillis()
            recod.uid = uid
            JdbcConnection.bootstrap.query<WebSocketMsgRecod>(recod).insert()
        }
    }

}