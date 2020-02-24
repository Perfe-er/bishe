package api;

import been.HttpResult;
import been.User;
import com.alibaba.fastjson.JSON;
import com.blabla.BaseDao;
import com.blabla.db.JdbcConnection;
import io.ktor.application.ApplicationCall;
import io.ktor.http.Parameters;
import io.ktor.request.ApplicationRequest;
import kotlin.coroutines.Continuation;
import online.sanen.cdm.api.condition.C;

import java.util.List;

public class UserDao extends BaseDao {

    public void login(ApplicationCall call, Continuation continuation)
    {
        //请求
        ApplicationRequest request = call.getRequest();
        //参数
        String name = request.getQueryParameters().get("name");
        //参数
        String passWd = request.getQueryParameters().get("passWd");

        List<User> users = JdbcConnection.bootstrap.queryTable(User.class).addCondition(
             c->{
                 c.add(C.eq("name",name));
                 c.add(C.eq("passWd",passWd));
             }).list(User.class);

        if(users.isEmpty()){
           writeGsonResponds(JSON.toJSONString(new HttpResult(400,"用户名或密码错误")),call,continuation);
        }else {
            writeGsonResponds(JSON.toJSONString(new HttpResult<User>(users.get(0),200,"登录成功")),call,continuation);
        }
    }

    public void listUser(ApplicationCall call, Continuation continuation){
        ApplicationRequest request = call.getRequest();
        List<User> users = JdbcConnection.bootstrap.queryTable(User.class).list(User.class);
        writeGsonResponds(JSON.toJSONString(new HttpResult<List<User>>(users,200,"")),call,continuation);

    }
}
