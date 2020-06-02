package root.mqtt.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.RO;
import root.mqtt.configure.MqttSendMessage;
import root.mqtt.service.TopicService;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * 测试网关 1319100003
 * @author cannon
 */
@RestController
@RequestMapping(value = "/Gateway")
public class GatewayController extends RO{
	
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
    	topicService.addTopicByGateway(pJson.getString("gatewayNumber"));
        return SuccessMsg("添加成功", "");
    }
    
    /**
	 * 删除网关
	 * @return
	 */
    @RequestMapping(value="/deleteGateway",produces = "text/plain;charset=UTF-8")
    public String deleteGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
    	topicService.removeTopicByGateway(pJson.getString("gatewayNumber"));
        return SuccessMsg("删除成功", "");
    }
    
    /**
	 * 修改网关配置
	 * @return
	 */
    @RequestMapping(value="/updateGatewayConfig",produces = "text/plain;charset=UTF-8")
    public String updateGatewayConfig(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
    	String gatewayNumber = pJson.getString("gatewayNumber");
    	String topic = "/001/"+ gatewayNumber + "/set";
    	pJson.remove("gatewayNumber");
    	mqttSendMessage.sendMessage(pJson.toJSONString(), topic);
        return SuccessMsg("修改成功", "");
    }
    
}
