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
@RequestMapping(value = "/reportServer/gateway")
public class GatewayController extends RO {

    @Resource
    private TopicService topicService;

    @Resource
    private MqttSendMessage mqttSendMessage;

    /**
     * 添加资产
     *
     * @return
     */
    @RequestMapping(value = "/CreateGateway", produces = "text/plain;charset=UTF-8")
    public String CreateGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {

        try {
            JSONObject  gatewayHeader =  pJson.getJSONObject("gatewayHeader");
            String imageBase64 = gatewayHeader.getString("imageBase64");
            gatewayHeader.put("image",imageBase64.getBytes());
            //获取网关id
            String gatewayId = gatewayHeader.getString("gateway_id");
            //检查网关是否已添加
            if(checkGateway(gatewayId)){
                return ErrorMsg("保存失败，网关已存在",null);
            }

            //插入头
            DbFactory.Open(DbFactory.FORM)
                    .insert("eam_gateway.createGatewayHeader", gatewayHeader);
            //插入行，先删除再增加
            JSONArray jsonArray = pJson.getJSONArray("gatewayLines");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                jsonObject.put("gateway_id",gatewayId);
                DbFactory.Open(DbFactory.FORM)
                        .insert("eam_gateway.createGatewayLines", jsonObject);
            }

            //添加监听
            topicService.addTopicByGateway(gatewayId);

            return SuccessMsg("保存成功", "");

        } catch (Exception ex) {
            return ExceptionMsg(ex.getMessage());
        }

    }

    @RequestMapping(value = "/UpdateGateway", produces = "text/plain;charset=UTF-8")
    public String UpdateGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        try {
            JSONObject  gatewayHeader =  pJson.getJSONObject("gatewayHeader");
            String imageBase64 = gatewayHeader.getString("imageBase64");
            gatewayHeader.put("image",imageBase64.getBytes());
            //获取网关id
            String gatewayId = gatewayHeader.getString("gateway_id");
            if(!checkGateway(gatewayId)){
                return ErrorMsg("更新失败，网关不存在","");
            }

            //更新头
            DbFactory.Open(DbFactory.FORM)
                    .update("eam_gateway.updateEamGateway", gatewayHeader);

            //删除网关关联的资产
            DbFactory.Open(DbFactory.FORM).delete("eam_gateway_asset.rmAssetByGatewayId",pJson.getJSONObject("gatewayHeader"));

            //重新插入关联资产
            JSONArray jsonArray = pJson.getJSONArray("gatewayLines");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                jsonObject.put("gateway_id",gatewayId);
                DbFactory.Open(DbFactory.FORM)
                        .insert("eam_gateway.createGatewayLines", jsonObject);
            }
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
                DbFactory.Open(DbFactory.FORM).delete("eam_gateway.rmEamGateway", jsonObject);
                //删除网关关联的资产
                DbFactory.Open(DbFactory.FORM).delete("eam_gateway_asset.rmAssetByGatewayId",jsonObject);

                topicService.removeTopicByGateway(jsonObject.getString("gateway_id"));
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
                    .selectOne("eam_gateway.getGatewayById", pJson.getString("gateway_id"));
            JSONObject jsonObject = (JSONObject) JSON.toJSON(map);
            return SuccessMsg("", jsonObject);
        } catch (Exception ex) {
            return ExceptionMsg(ex.getMessage());
        }
    }

    private boolean checkGateway(String gateway_id){
        HashMap<String, Object> map = DbFactory.Open(DbFactory.FORM)
                .selectOne("eam_gateway.getGatewayById",gateway_id);
       return map!=null && !map.isEmpty();

    }

    /**
     * 通过网关获取资产
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getGatewayAssetById", produces = "text/plain;charset=UTF-8")
    public String getGatewayAssetById(@RequestBody JSONObject pJson) {

        try {
            List<HashMap<String, Object>> list = DbFactory.Open(DbFactory.FORM)
                    .selectList("eam_gateway.getGatewayAssetById", pJson);
            JSONArray jSONArray = (JSONArray) JSON.toJSON(list);
            return SuccessMsg("", jSONArray);
        } catch (Exception ex) {
            return ExceptionMsg(ex.getMessage());
        }
    }


    /**
     * 添加网关
     *
     * @return
     */
    @RequestMapping(value = "/addGateway", produces = "text/plain;charset=UTF-8")
    public String addGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int gatewayId = pJson.getIntValue("gateway_id");
        int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_gateway.queryCountByGatewayId", gatewayId);
        if (0 < count) {
            return ExceptionMsg("网关已添加,请勿重复添加");
        } else {
            DbFactory.Open(DbFactory.FORM).insert("eam_gateway.addEamGateway", pJson);
            topicService.addTopicByGateway(pJson.getString("gatewayNumber"));
            return SuccessMsg("保存成功", "");
        }
    }

    /**
     * 删除网关
     *
     * @return
     */
    @RequestMapping(value = "/deleteGateway", produces = "text/plain;charset=UTF-8")
    public String deleteGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int gatewayId = pJson.getIntValue("gateway_id");
        int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_gateway.queryCountByGatewayId", gatewayId);
        if (0 < count) {
            DbFactory.Open(DbFactory.FORM).insert("eam_gateway.rmEamGateway", gatewayId);
            topicService.removeTopicByGateway(pJson.getString("gatewayNumber"));
        }
        return SuccessMsg("删除成功", "");
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
        List<Map<String, Object>> gatewayList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway.listEamGatewayByPage", map);
        int total = DbFactory.Open(DbFactory.FORM).selectOne("eam_gateway.countEamGatewayByPage", map);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("list", gatewayList);
        map3.put("total", total);
        map2.put("msg", "查询成功");
        map2.put("data", map3);
        map2.put("status", 0);
        return JSON.toJSONString(map2);
    }

    @RequestMapping(value = "/listEamGatewayAll", produces = "text/plain;charset=UTF-8")
    public String listEamGatewayAll(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> gatewayList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway.listEamGateway", pJson);
        return SuccessMsg("查询成功", gatewayList);
    }

    /**
     * 修改网关配置
     *
     * @return
     */
    @RequestMapping(value = "/updateGatewayConfig", produces = "text/plain;charset=UTF-8")
    public String updateGatewayConfig(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        String gatewayNumber = pJson.getString("gateway_id");
        String topic = "/001/" + gatewayNumber + "/set";
        pJson.remove("gatewayNumber");
        mqttSendMessage.sendMessage(pJson.toJSONString(), topic);
        return SuccessMsg("修改成功", "");
    }

    /**
     * 网关关联资产
     *
     * @return
     */
    @RequestMapping(value = "/bindAssetList", produces = "text/plain;charset=UTF-8")
    public String bindAssetList(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        JSONArray array = pJson.getJSONArray("data");
        String gatewayId = pJson.getString("gateway_id");
        SqlSession session = DbFactory.Open(false, DbFactory.FORM);
        array.forEach((c) -> {
            JSONObject child = (JSONObject) c;
            child.put("gateway_id", gatewayId);
            session.insert("eam_gateway_asset.bingAssetByGateway", child);
        });
        return SuccessMsg("关联成功", "");
    }


    @RequestMapping(value = "/treeGatewayByAddressId", produces = "text/plain;charset=UTF-8")
    public String treeGatewayByAddressId(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> gatewayList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway.treeGatewayByAddressId", pJson);
        return JSON.toJSONString(gatewayList);
    }

}
