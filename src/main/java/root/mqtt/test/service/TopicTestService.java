package root.mqtt.test.service;

import org.springframework.stereotype.Service;
import root.mqtt.configure.MyMqttPahoMessageDrivenChannelAdapter;
import root.mqtt.test.configure.MyMqttPahoMessageDrivenChannelTestAdapter;

import javax.annotation.Resource;


@Service
public class TopicTestService {
	
	@Resource
	private MyMqttPahoMessageDrivenChannelTestAdapter myMqttPahoMessageDrivenChannelTestAdapter;
	
	/**
	 * 按网关添加订阅
	 * @param gateway
	 */
	public void addTopicByGateway(String gateway) {
		myMqttPahoMessageDrivenChannelTestAdapter.addTopicByGateway(gateway);
	}

	/**
	 * 按网关移除订阅
	 * @param gateway
	 */
	public void removeTopicByGateway(String gateway) {
		myMqttPahoMessageDrivenChannelTestAdapter.removeTopicByGateway(gateway);
	}
}
