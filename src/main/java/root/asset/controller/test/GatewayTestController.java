package root.asset.controller.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.mqtt.configure.MqttSendMessage;
import root.mqtt.service.TopicService;
import root.mqtt.test.service.TopicTestService;
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
 * @author cannon
 */
@RestController
@RequestMapping(value = "/reportServer/test/gateway")
public class GatewayTestController extends RO {

    @Resource
    private TopicTestService topicTestService;

    /**
     * 添加网关
     * @return
     */
    @RequestMapping(value = "/CreateGateway", produces = "text/plain;charset=UTF-8")
    public String CreateGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {

        try {
            JSONObject  gatewayHeader =  pJson.getJSONObject("gatewayHeader");
            //获取网关id
            String gatewayId = gatewayHeader.getString("gateway_id");
            //检查网关是否已添加
            if(checkGateway(gatewayId)){
                return ErrorMsg("保存失败，网关已存在",null);
            }

            //插入头
            DbFactory.Open(DbFactory.FORM)
                    .insert("eam_gateway_test.createGatewayHeader", gatewayHeader);
            //添加监听
            topicTestService.addTopicByGateway(gatewayId);

            return SuccessMsg("保存成功", "");

        } catch (Exception ex) {
            return ExceptionMsg(ex.getMessage());
        }

    }

    @RequestMapping(value = "/UpdateGateway", produces = "text/plain;charset=UTF-8")
    public String UpdateGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        try {
            JSONObject  gatewayHeader =  pJson.getJSONObject("gatewayHeader");
            //获取网关id
            String gatewayId = gatewayHeader.getString("gateway_id");
            if(!checkGateway(gatewayId)){
                return ErrorMsg("更新失败，网关不存在","");
            }

            //更新头
            DbFactory.Open(DbFactory.FORM)
                    .update("eam_gateway_test.updateEamGateway", gatewayHeader);
            return SuccessMsg("更新成功", "");

        } catch (Exception ex) {
            return ExceptionMsg(ex.getMessage());
        }
    }

    /**
     * 删除网关
     * @param pJson
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/DeleteGateway", produces = "text/plain;charset=UTF-8")
    public String DeleteGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        try {
            JSONArray jsonArray = pJson.getJSONArray("gatewayLines");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //删除网关
                DbSession.delete("eam_gateway_test.rmEamGateway", jsonObject);
                topicTestService.removeTopicByGateway(jsonObject.getString("gateway_id"));
            }
            return SuccessMsg("删除成功", "");
        } catch (Exception ex) {
            return ExceptionMsg(ex.getMessage());
        }
    }
    
    @RequestMapping(value = "/getGatewayById", produces = "text/plain;charset=UTF-8")
    public String getGatewayById(@RequestBody JSONObject pJson) {

        try {
            HashMap<String, Object> map = DbFactory.Open(DbFactory.FORM)
                    .selectOne("eam_gateway_test.getGatewayById", pJson.getString("gateway_id"));
            JSONObject jsonObject = (JSONObject) JSON.toJSON(map);
            return SuccessMsg("", jsonObject);
        } catch (Exception ex) {
            return ExceptionMsg(ex.getMessage());
        }
    }

    private boolean checkGateway(String gateway_id){
        HashMap<String, Object> map = DbFactory.Open(DbFactory.FORM)
                .selectOne("eam_gateway_test.getGatewayById",gateway_id);
       return map!=null && !map.isEmpty();
    }

    /**
     * 通过网关获取资产
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getGatewayTagList", produces = "text/plain;charset=UTF-8")
    public String getGatewayTagList(@RequestBody JSONObject pJson) {

        try {
            List<HashMap<String, Object>> list = DbFactory.Open(DbFactory.FORM)
                    .selectList("eam_asset_status_test.getGatewayTagList", pJson);
            JSONArray jSONArray = (JSONArray) JSON.toJSON(list);
            return SuccessMsg("", jSONArray);
        } catch (Exception ex) {
            return ExceptionMsg(ex.getMessage());
        }
    }

    /**
     * 获取网关列表
     *
     * @return
     */
    @RequestMapping(value = "/listEamGateway", produces = "text/plain;charset=UTF-8")
    public String listEamGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int currentPage = Integer.valueOf(pJson.getString("pageNum"));
        int perPage = Integer.valueOf(pJson.getString("perPage"));

        if (1 == currentPage || 0 == currentPage) {
            currentPage = 0;
        } else {
            currentPage = (currentPage - 1) * perPage;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startIndex", currentPage);
        map.put("perPage", perPage);
        map.put("keyword",pJson.getString("keyword"));
        List<Map<String, Object>> gatewayList = DbSession.selectList("eam_gateway_test.listEamGatewayByPage", map);
        int total = DbSession.selectOne("eam_gateway_test.countEamGatewayByPage", map);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("list", gatewayList);
        map3.put("total", total);
        map2.put("msg", "查询成功");
        map2.put("data", map3);
        map2.put("status", 0);
        return JSON.toJSONString(map2);
    }



}
