package root.mqtt.configure;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * 消息抽象类
 * @author cannon
 *
 */
public interface HandlerMessage{
	
	void handleMessage(Message<byte[]> message) throws MessagingException;

}
