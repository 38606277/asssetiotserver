package root.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.mqtt.configure.MqttSendMessage;
import root.mqtt.service.TopicService;
import root.report.common.DbSession;
import root.report.common.RO;
import root.report.db.DbFactory;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试网关 1319100003
 *
 * @author cannon
 */
@RestController
@RequestMapping(value = "/reportServer/alarm")
public class AlarmController extends RO {


    /**
     * 获取警告列表
     *
     * @return
     */
    @RequestMapping(value = "/listEamAlarm", produces = "text/plain;charset=UTF-8")
    public String listEamGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int currentPage = Integer.valueOf(pJson.getString("pageNum"));
        int perPage = Integer.valueOf(pJson.getString("perPage"));

        if (1 == currentPage || 0 == currentPage) {
            currentPage = 0;
        } else {
            currentPage = (currentPage - 1) * perPage;
        }
        pJson.put("startIndex", currentPage);
        pJson.put("perPage", perPage);
        List<Map<String, Object>> gatewayList = DbSession.selectList("eam_alarm.listEamAlarmByPage", pJson);
        int total = DbSession.selectOne("eam_alarm.countEamAlarmByPage", pJson);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("list", gatewayList);
        map3.put("total", total);
        map2.put("msg", "查询成功");
        map2.put("data", map3);
        map2.put("status", 0);
        return JSON.toJSONString(map2);
    }


    /**
     * 更新状态
     * @return
     */
    @RequestMapping(value = "/updateAlarmStatus", produces = "text/plain;charset=UTF-8")
    public String updateAlarmStatus(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        DbSession.update("eam_alarm.updateAlarmStatus", pJson);
        return SuccessMsg("修改成功", "");
    }

}
