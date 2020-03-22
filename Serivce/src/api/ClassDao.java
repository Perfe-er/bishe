package api;

import been.Class;
import been.HttpResult;
import been.User;
import com.alibaba.fastjson.JSON;
import db.JdbcConnection;
import io.ktor.application.ApplicationCall;
import io.ktor.request.ApplicationRequest;
import kotlin.coroutines.Continuation;
import online.sanen.cdm.api.condition.C;

import java.util.ArrayList;
import java.util.List;

public class ClassDao extends BaseDao {



    public Class getClassByClassId(int classID) {

        List<Class> cs = JdbcConnection.bootstrap.queryTable(Class.class).addCondition(
                c -> {
                    c.add(C.eq("classID", classID));
                }
        ).list(Class.class);

        Class c = null;
        if (!cs.isEmpty()) {
            c = cs.get(0);
        }
       return c;
    }

    public void findClassByClassName(ApplicationCall call, Continuation continuation) {
        ApplicationRequest request = call.getRequest();
        String className = request.getQueryParameters().get("className");//className
        List<Class> cs = JdbcConnection.bootstrap.queryTable(Class.class).addCondition(
                c -> {
                    c.add(C.eq("className", className));
                }
        ).list(Class.class);

        Class c = null;
        if (!cs.isEmpty()) {
            c = cs.get(0);
        }
        writeGsonResponds(JSON.toJSONString(new HttpResult<Class>(c, 200, "")), call, continuation);
    }

    /**
     * 创建班级
     *
     * @param call
     * @param continuation
     */
    public void createClass(ApplicationCall call, Continuation continuation) {
        ApplicationRequest request = call.getRequest();
        String className = request.getQueryParameters().get("className");//className
        String founderId = request.getQueryParameters().get("founderId");//

        Class c = new Class();
        c.setClassName(className);
        c.setFounderId(Integer.parseInt(founderId));
        int cid = JdbcConnection.bootstrap.query(c).insert();
        c.setClassID(cid);
        writeGsonResponds(JSON.toJSONString(new HttpResult<Class>(c, 200, "创建成功")), call, continuation);
    }


    public void deleteClass(ApplicationCall call, Continuation continuation) {

        ApplicationRequest request = call.getRequest();
        String classID = request.getQueryParameters().get("classID");//className

        Class c = new Class();
        c.setClassID(Integer.parseInt(classID));
        try {
            JdbcConnection.bootstrap.query(c).delete();
            writeGsonResponds(JSON.toJSONString(new HttpResult(200, "删除成功")), call, continuation);
        } catch (Exception e) {
            e.printStackTrace();
            writeGsonResponds(JSON.toJSONString(new HttpResult(400, "删除失败　" + e.getMessage())), call, continuation);
        }
    }


    public void modifyClass(ApplicationCall call, Continuation continuation) {
        ApplicationRequest request = call.getRequest();
        String classID = request.getQueryParameters().get("classID");//className
        String className = request.getQueryParameters().get("className");//className
        Class c = new Class();
        c.setClassID(Integer.parseInt(classID));
        c.setClassName(className);

        try {
            JdbcConnection.bootstrap.query(c).setFields("className").update();
            writeGsonResponds(JSON.toJSONString(new HttpResult(200, "修改成功")), call, continuation);
        } catch (Exception e) {
            e.printStackTrace();
            writeGsonResponds(JSON.toJSONString(new HttpResult(400, "修改失败　" + e.getMessage())), call, continuation);
        }

    }

}
