package root.mqtt.service;

public interface MqttReceiveService {
	
	void handlerMqttMessage(String topic, byte[] payload);
}
