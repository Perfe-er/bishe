package api;

import been.HttpResult;
import been.User;
import com.alibaba.fastjson.JSON;


import com.hapi.Auth;
import db.JdbcConnection;
import io.ktor.application.ApplicationCall;
import io.ktor.request.ApplicationRequest;
import kotlin.coroutines.Continuation;
import online.sanen.cdm.api.condition.C;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class UserDao extends BaseDao {


    public User getUserById(int id) {
        List<User> users = JdbcConnection.bootstrap.queryTable(User.class).addCondition(
                c -> {
                    c.add(C.eq("id", id));
                }
        ).list(User.class);
        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }


    public List<User> getStudentByClassId(int calssId){

        List<User> users = JdbcConnection.bootstrap.queryTable(User.class).addCondition(
                c -> {
                    c.add(C.eq("classID", calssId));
                }
        ).list(User.class);
        System.out.println("getStudentByClassId"+calssId+" "+users.size());
        return users;
    }


    public void getUserInfoById(ApplicationCall call, Continuation continuation) {
        ApplicationRequest request = call.getRequest();
        String id = request.getQueryParameters().get("id");
        if(TextUtils.isEmpty(id)){
            writeGsonResponds(JSON.toJSONString(new HttpResult(400, "ｉｄ need")), call, continuation);
        }
        User u = getUserById(Integer.parseInt(id));
        u.setPassWd("");
        writeGsonResponds(JSON.toJSONString(new HttpResult<User>(u, 200, "注册成功")), call, continuation);
    }


    //注册
    public void register(ApplicationCall call, Continuation continuation) {
        ApplicationRequest request = call.getRequest();
        String phone = request.getQueryParameters().get("phone");
        String passWd = request.getQueryParameters().get("passWd");
        List<User> users = JdbcConnection.bootstrap.queryTable(User.class).addCondition(
                c -> {
                    c.add(C.eq("phone", phone));
                }
        ).list(User.class);
        if (users.isEmpty()) {
            User user = new User();
            user.setPhone(phone);
            user.setPassWd(passWd);
            int user1 = JdbcConnection.bootstrap.query(user).insert();
            user.setId(user1);
            String t = Auth.INSTANCE.sign(user.getId() + user.getName()).get("token");
            user.setToken(t);
            writeGsonResponds(JSON.toJSONString(new HttpResult<User>(user, 200, "注册成功")), call, continuation);

        } else {
            writeGsonResponds(JSON.toJSONString(new HttpResult(400, "用户已存在")), call, continuation);
        }
    }

    //登录
    public void login(ApplicationCall call, Continuation continuation) {
        //请求
        ApplicationRequest request = call.getRequest();
        //客户端参数
        String phone = request.getQueryParameters().get("phone");
        //客户端参数
        String passWd = request.getQueryParameters().get("passWd");

        List<User> users = JdbcConnection.bootstrap.queryTable(User.class).addCondition(
                c -> {
                    c.add(C.eq("phone", phone));
                    c.add(C.eq("passWd", passWd));
                }).list(User.class);

        if (users.isEmpty()) {
            writeGsonResponds(JSON.toJSONString(new HttpResult(400, "用户名或密码错误")), call, continuation);
        } else {
            User u = users.get(0);
            String t = Auth.INSTANCE.sign(u.getId() + u.getName()).get("token");
            u.setToken(t);
            writeGsonResponds(JSON.toJSONString(new HttpResult<User>(u, 200, "登录成功")), call, continuation);
        }
    }

    public void infoEdit(ApplicationCall call, Continuation continuation) {
        //请求
        ApplicationRequest request = call.getRequest();
        String id = request.getQueryParameters().get("id");
        String stuID = request.getQueryParameters().get("stuID");
        String name = request.getQueryParameters().get("name");
        String sex = request.getQueryParameters().get("sex");
        String college = request.getQueryParameters().get("college");
        String className = request.getQueryParameters().get("className");
        String classID = request.getQueryParameters().get("classID");
        String number = request.getQueryParameters().get("number");
        String parentPho = request.getQueryParameters().get("parentPho");
        String identity = request.getQueryParameters().get("identity");
        String address = request.getQueryParameters().get("address");
        String birthday = request.getQueryParameters().get("birthday");
        String stuType = request.getQueryParameters().get("stuType");

        int id1 = Integer.parseInt(id);
        int sex1 = Integer.parseInt(sex);
        int classID1 = Integer.parseInt(classID);
        int number1 = Integer.parseInt(number);
        int stuType1 = Integer.parseInt(stuType);
        User user = new User();

        user.setId(id1);
        user.setStuID(stuID);
        user.setName(name);
        user.setSex(sex1);
        user.setCollege(college);
        user.setClassName(className);
        user.setClassID(classID1);
        user.setNumber(number1);
        user.setParentPho(parentPho);
        user.setIdentity(identity);
        user.setAddress(address);
        user.setBirthday(birthday);
        user.setStuType(stuType1);

        JdbcConnection.bootstrap.query(user).setFields("college", "className", "classID", "number", "parentPho", "identity"
                , "address", "birthday", "stuType", "stuID", "name", "sex").update();
        writeGsonResponds(JSON.toJSONString(new HttpResult<User>(user, 200, "修改成功")), call, continuation);

    }

    /**
     * 修改密码
     * @param call
     * @param continuation
     */
    public void modifyPassWd(ApplicationCall call, Continuation continuation) {

        //请求
        ApplicationRequest request = call.getRequest();
        String id = request.getQueryParameters().get("id");
        String oldPwd = request.getQueryParameters().get("oldPwd");
        String newPwd = request.getQueryParameters().get("newPwd");

        User user = getUserById(Integer.parseInt(id));
        if (user == null) {
            writeGsonResponds(JSON.toJSONString(new HttpResult(400, "用户id错误")), call, continuation);
            return;
        }
        if (!user.getPassWd().equals(oldPwd)) {
            writeGsonResponds(JSON.toJSONString(new HttpResult(400, "旧密码错误")), call, continuation);
            return;
        }
        user.setPassWd(newPwd);
        JdbcConnection.bootstrap.query(user).setFields("passWd").update();
        writeGsonResponds(JSON.toJSONString(new HttpResult<User>(user, 200, "修改成功")), call, continuation);

    }





}
