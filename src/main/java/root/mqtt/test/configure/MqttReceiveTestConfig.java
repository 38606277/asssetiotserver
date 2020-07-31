package root.mqtt.test.configure;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import root.mqtt.configure.MyMqttPahoMessageDrivenChannelAdapter;
import root.mqtt.service.MqttReceiveService;
import root.mqtt.test.service.MqttReceiveServiceTestImpl;

import javax.annotation.Resource;

/**
 * 接收者配置
 * @author cannon
 *
 */
@Configuration
@IntegrationComponentScan
public class MqttReceiveTestConfig {

	@Value("${mqttTest.username}")
	private String username;

	@Value("${mqttTest.password}")
	private String password;

	@Value("${mqttTest.host}")
	private String hostUrl;

	@Value("${mqttTest.clientinid}")
	private String clientId;

	@Value("${mqttTest.topic}")
	private String defaultTopic;

	@Value("${mqttTest.timeout}")
	private int completionTimeout ;   //连接超时

	@Resource
	private MqttReceiveService mqttReceiveTestService;
	
	@Bean
	public MqttConnectOptions getMqttConnectOptionsTest(){
		MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
		mqttConnectOptions.setCleanSession(true);
		mqttConnectOptions.setConnectionTimeout(10);
		mqttConnectOptions.setKeepAliveInterval(90);
		mqttConnectOptions.setAutomaticReconnect(true);
		mqttConnectOptions.setUserName(username);
		mqttConnectOptions.setPassword(password.toCharArray());
		mqttConnectOptions.setServerURIs(new String[]{hostUrl});
		mqttConnectOptions.setKeepAliveInterval(2);
		return mqttConnectOptions;
	}
	
	@Bean
	public MqttPahoClientFactory mqttClientFactoryTest() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setConnectionOptions(getMqttConnectOptionsTest());
		return factory;
	}

	//接收通道
	@Bean
	public MessageChannel mqttInputChannelTest() {
		return new DirectChannel();
	}
	
	//配置client,监听的topic /001/1319100003/bt
	@Bean
	public MyMqttPahoMessageDrivenChannelTestAdapter inboundTest() {
		MyMqttPahoMessageDrivenChannelTestAdapter adapter = new MyMqttPahoMessageDrivenChannelTestAdapter(clientId+"_inbound", mqttClientFactoryTest(),
				defaultTopic);
		//adapter.addTopic(getTopic("1319100003"));
		adapter.setCompletionTimeout(completionTimeout);
		DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
		converter.setPayloadAsBytes(true);
		adapter.setConverter(converter);
		adapter.setQos(1);
		adapter.setOutputChannel(mqttInputChannelTest());
		System.err.println("mqtt 测试适配器设置成功");
		return adapter;
	}

	//通过通道获取数据
	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannelTest")
	public MessageHandler handlerTest() {
		return new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {

				String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
				long timestamp = message.getHeaders().getTimestamp();
				if(topic!=null && 0 < topic.trim().length() ) {
					byte[] payload = (byte[])message.getPayload();
					mqttReceiveTestService.handlerMqttMessage(topic,  payload,timestamp);
				}else {
					System.out.println("mqttInputChannelTest通道 topic为空");
				}	
			}
		};
	}

	

}
