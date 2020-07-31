package root.mqtt.test.configure;

import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyMqttPahoMessageDrivenChannelTestAdapter extends MqttPahoMessageDrivenChannelAdapter{
	private static final String TAG = "MyMqttPahoMessageDrivenChannelTestAdapter";
	private List<String> topicList = new ArrayList<>();

	public MyMqttPahoMessageDrivenChannelTestAdapter(String clientId, MqttPahoClientFactory clientFactory, String string) {
		super(clientId, clientFactory, string);
		// TODO Auto-generated constructor stub
	}

	public boolean contains(String gateway){
		return topicList.contains(gateway);
	}

	/**
	 * 添加订阅
	 * @param gateway
	 */
	public void addTopicByGateway(String gateway) {
		if(contains(gateway)){
			System.out.println(TAG + " : " + gateway + " 已存在，无需重复添加");
			return ;
		}

		System.out.println(TAG + " : " + "addTopicByGateway ：" + gateway);
		String[] topicArr = getTopic(gateway);
		for(String topic:topicArr) {
			this.addTopic(topic,1);
		}
		topicList.add(gateway);
	}

	/**
	 * 删除订阅
	 * @param gateway
	 */
	public void removeTopicByGateway(String gateway) {
		if(!contains(gateway)){
			System.out.println(TAG + " : " + gateway + " 不存在，无需重复移除");
			return ;
		}

		System.out.println(TAG + " : " + "removeTopicByGateway ：" + gateway);
		this.removeTopic(getTopic(gateway));
		topicList.remove(gateway);
	}

	private String[] getTopic(String number) {
		return  new String[] {
				"/001/"+number+"/register",
				"/001/"+number+"/update",
				"/001/"+number+"/bt",
				"/001/"+number+"/set_res",
				"/001/"+number+"/alarm"
		};
	}

}
