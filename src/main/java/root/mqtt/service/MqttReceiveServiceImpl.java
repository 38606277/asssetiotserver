package root.mqtt.service;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import root.mqtt.bean.*;
import root.mqtt.util.HexUtils;
import root.report.db.DbFactory;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 消息接收
 * @author cannon
 *
 */
@Service("mqttReceiveService")
public class MqttReceiveServiceImpl implements MqttReceiveService{

	@Override
	public void handlerMqttMessage(String topic, byte[] payload,long timestamp) {
		// TODO Auto-generated method stub
	
		System.out.println("topic：" + topic);
		
		String[] data  = topic.substring(1).split("/");
		
		if(data.length !=3) {
			System.out.println("非物联网订阅信息：" + topic);
			return ;
		}

		String prefix = data[0];//固定前缀
		long gatewayId = Long.parseLong(data[1]);//网关编号
		String type = data[2];//订阅类型

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String receiveTime = df.format(new Date(timestamp));
		if("bt".equals(type)){
			List<MQTTBtMessage> labelList = decodeLableList(payload);
			
			for(MQTTBtMessage mqttBtMessage :labelList) {
				Map<String,Object> map = new HashMap<>();
				map.put("gateway_id",gatewayId);
				map.put("tag_id",mqttBtMessage.getCode());
				map.put("receive_time",receiveTime);
				map.put("electricity",mqttBtMessage.getElectricity());
				map.put("signalIntensity",mqttBtMessage.getSignalIntensity());

				int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset_status.queryCountByTagId",map);
				if(0 < count){
					//更新
					DbFactory.Open(DbFactory.FORM).update("eam_asset_status.updateEamAssetStatus",map);
				}else{
					//添加
					DbFactory.Open(DbFactory.FORM).insert("eam_asset_status.addEamAssetStatus",map);
				}
			}
		
			System.out.println(labelList.toString());
		}else if("update".equals(type)){
			Map<String, Object> mapObj = JSON.parseObject(new String (payload));
			MQTTUpdateMessage mqttUpdateMessage = new MQTTUpdateMessage(mapObj);
			System.out.println(mqttUpdateMessage.toString());
		}else if("register".equals(type)) {
			MQTTRegisterMessage mqttRegisterMessageBean  = JSON.parseObject(payload, MQTTRegisterMessage.class);
			System.out.println(mqttRegisterMessageBean.toString());
		}else if("set_res".equals(type)){
			MQTTSetResMessage mqttSetRexMessageBean  = JSON.parseObject(payload, MQTTSetResMessage.class);
			System.out.println(mqttSetRexMessageBean.toString());
		}else if("alarm".equals(type)){
			MQTTAlarmMessage  mqttAlarmMessageBean = JSON.parseObject(payload, MQTTAlarmMessage.class);
			System.out.println(mqttAlarmMessageBean.toString());
		}else{
			System.out.println(new String (payload));
		}
	}
	
	/**
	 * 解析电子标签集合
	 * @param payload
	 * @return
	 */
	private List<MQTTBtMessage> decodeLableList(byte[] payload) {
		List<MQTTBtMessage> labelList = new ArrayList<MQTTBtMessage>();
		int span = 5;
		int count = payload.length/span;
		String hexBinary = DatatypeConverter.printHexBinary(payload);
		System.out.println("接收到" + count + "个标签数据 ---- 十六进制 ： " + hexBinary  );

		for(int i = 0 ; i <count ; i++) {
			byte [] bytes = new byte[span];
			for(int j = 0 ; j<span; j++) {
				bytes[j] = payload[i*span+j];
			}
			MQTTBtMessage label = decodeLabel(bytes);
			labelList.add(label);
		}
		return labelList;
	}

	
	/**
	 * 解析单个标签
	 * @param bytes
	 * @return
	 */
	public static MQTTBtMessage decodeLabel(byte[] bytes) {
		//byte数组转换为二进制字符串
		String bitStr =	HexUtils.byteArrToBinStr(bytes);
		//电量
		int electricityBit = Integer.parseInt(bitStr.substring(0,4),2);
		String electricityStr = ((electricityBit + 20)/10f) + "V";
		//编号
		int number  = Integer.parseInt(bitStr.substring(4,32),2);
		//信号强度
		int signalBit = Integer.parseInt(bitStr.substring(32),2);
		String signalStr =  ((byte)signalBit) + "db";

		//decode(bitStr.substring(32));

		MQTTBtMessage label = new  MQTTBtMessage();
		label.setCode(String.valueOf(number));
		label.setElectricity(electricityBit);
		label.setSignalIntensity(signalBit);

		return label;
	}

}
