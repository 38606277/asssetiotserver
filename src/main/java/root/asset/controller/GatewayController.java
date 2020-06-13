package root.asset.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import root.mqtt.configure.MqttSendMessage;
import root.mqtt.service.TopicService;
import root.report.common.RO;
import root.report.db.DbFactory;

/**
 * 测试网关 1319100003
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
	 * 添加网关
	 * @return
	 */
    @RequestMapping(value="/addGateway",produces = "text/plain;charset=UTF-8")
    public String addGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
    	int gatewayId = pJson.getIntValue("gateway_id");
		int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_gateway.queryCountByGatewayId",gatewayId);
		if(0 < count){
			return ExceptionMsg("网关已添加,请勿重复添加");
		}else{
			DbFactory.Open(DbFactory.FORM).insert("eam_gateway.addEamGateway",pJson);
			topicService.addTopicByGateway(pJson.getString("gatewayNumber"));
			return SuccessMsg("保存成功", "");
		}
    }
    
    /**
	 * 删除网关
	 * @return
	 */
    @RequestMapping(value="/deleteGateway",produces = "text/plain;charset=UTF-8")
    public String deleteGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
		int gatewayId = pJson.getIntValue("gateway_id");
		int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_gateway.queryCountByGatewayId",gatewayId);
		if(0 < count){
			DbFactory.Open(DbFactory.FORM).insert("eam_gateway.rmEamGateway",gatewayId);
			topicService.removeTopicByGateway(pJson.getString("gatewayNumber"));
		}
        return SuccessMsg("删除成功", "");
    }

	/**
	 * 获取网关列表
	 * @return
	 */
	@RequestMapping(value="/listEamGateway",produces = "text/plain;charset=UTF-8")
	public String listEamGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
		int currentPage=Integer.valueOf(pJson.getString("pageNum"));
		int perPage=Integer.valueOf(pJson.getString("perPage"));
		if(1==currentPage|| 0==currentPage){
			currentPage=0;
		}else{
			currentPage=(currentPage-1)*perPage;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("startIndex", currentPage);
		map.put("perPage",perPage);
		List<Map<String,Object>> gatewayList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway.listEamGatewayByPage",map);
		int total=DbFactory.Open(DbFactory.FORM).selectOne("eam_gateway.countEamGateway", map);
		Map<String,Object> map2 =new HashMap<String,Object>();
		Map<String,Object> map3 =new HashMap<String,Object>();
		map3.put("list",gatewayList);
		map3.put("total",total);
		map2.put("msg","查询成功");
		map2.put("data",map3);
		map2.put("status",0);
		return JSON.toJSONString(map2);
	}

	@RequestMapping(value="/listEamGatewayAll",produces = "text/plain;charset=UTF-8")
	public String listEamGatewayAll(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
		List<Map<String,Object>> gatewayList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway.listEamGateway",pJson);
		return SuccessMsg("查询成功",gatewayList);
	}

    /**
	 * 修改网关配置
	 * @return
	 */
    @RequestMapping(value="/updateGatewayConfig",produces = "text/plain;charset=UTF-8")
    public String updateGatewayConfig(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
    	String gatewayNumber = pJson.getString("gateway_id");
    	String topic = "/001/"+ gatewayNumber + "/set";
    	pJson.remove("gatewayNumber");
    	mqttSendMessage.sendMessage(pJson.toJSONString(), topic);
        return SuccessMsg("修改成功", "");
    }

	/**
	 * 网关关联资产
	 * @return
	 */
	@RequestMapping(value="/bindAssetList",produces = "text/plain;charset=UTF-8")
	public String bindAssetList(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
		JSONArray array = pJson.getJSONArray("data");
		String gatewayId = pJson.getString("gateway_id");
		SqlSession session = DbFactory.Open(false,DbFactory.FORM);
		array.forEach((c)->{
			JSONObject child = (JSONObject)c;
			child.put("gateway_id",gatewayId);
			session.insert("eam_gateway_asset.bingAssetByGateway", child);
		});
		return SuccessMsg("关联成功", "");
	}

}
