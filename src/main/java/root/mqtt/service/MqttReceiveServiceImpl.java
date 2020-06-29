package root.mqtt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.mqtt.bean.*;
import root.mqtt.util.HexUtils;
import root.report.db.DbFactory;
import root.report.service.webchat.HttpRequestUtil;
import root.report.temperature.http;

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

	@Autowired
	private HttpRequestUtil httpRequestUtil;

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
			List<MQTTBtMessage> labelList = decodeLabelList(payload);
			
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
				buildAlarm(String.valueOf(gatewayId),mqttBtMessage);
			}

			System.out.println(labelList.toString());
		}else if("update".equals(type)){
			Map<String, Object> mapObj = JSON.parseObject(new String (payload));
			MQTTUpdateMessage mqttUpdateMessage = new MQTTUpdateMessage(mapObj);
			float lng  = 0;
			float lat  = 0;

			if(mqttUpdateMessage.getLongitudeGPS() != 0 && mqttUpdateMessage.getLatitudeGPS()!=0){
				//GPS经纬度
				lng = mqttUpdateMessage.getLongitudeGPS();
				lat = mqttUpdateMessage.getLatitudeGPS();
			}else if(mqttUpdateMessage.getLongitudeLBS() != 0 && mqttUpdateMessage.getLatitudeLBS()!=0){
				//基站经纬度
				lng = mqttUpdateMessage.getLongitudeLBS();
				lat = mqttUpdateMessage.getLatitudeLBS();
			}else if(!"NULL".equals(mqttUpdateMessage.getWifiMac())){
				//通过wifi去获取经纬度
			}else if(!"NULL".equals(mqttUpdateMessage.getBaseStationInfo())){
				//通过基站信息获取经纬度
				String url = "http://apilocate.amap.com/position";
				String params = mqttUpdateMessage.getBaseStationInfo()+"&accesstype=0&output=json&key=f741b107dc26ffed2f7e332de0f4172c";
				String response = http.sendGet(url,params);
				System.out.println("基站获取定位结果：" + response);

				JSONObject  jsonObject = JSONObject.parseObject(response);
				if("1".equals(jsonObject.getString("status"))){//定位成功
					String location = jsonObject.getJSONObject("result").getString("location");
					String[] locationArr = location.split(",");
					lng = Float.parseFloat(locationArr[0]);
					lat = Float.parseFloat(locationArr[1]);
				}
			}

			Map<String, Object> dataMap = mqttUpdateMessage.getDataMap();
			dataMap.put("gateway_id",gatewayId);
			//定位优先级：GPS>WIFI>LBS
			dataMap.put("lng",lng);//结果经纬度
			dataMap.put("rng",lat);//结果经纬度
			dataMap.put("receive_time",receiveTime);

			int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_gateway_status.queryCountByGatewayId",dataMap);
			if(0 < count){
				//更新
				DbFactory.Open(DbFactory.FORM).update("eam_gateway_status.updateEamGatewayStatus",dataMap);
			}else{
				//添加
				DbFactory.Open(DbFactory.FORM).insert("eam_gateway_status.addEamGatewayStatus",dataMap);
			}

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
	private List<MQTTBtMessage> decodeLabelList(byte[] payload) {
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

	/**
	 * 构建报警记录
	 */
	private void buildAlarm(String gatewayId,MQTTBtMessage mqttBtMessage){
		//获取资产数据
		Map result =DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.listEamAssetByIotNum",new HashMap<String,Object>(){
			{
				put("iot_num",mqttBtMessage.getCode());
			}
		});
		if(result==null || result.size() == 0){
			return ;
		}


		/**
		 * 电压警告
		 */
		if(mqttBtMessage.getElectricity()< 12){ //电压低
			HashMap map = new HashMap<String,Object>();
			map.put("alarm_num","DYD" + System.currentTimeMillis());
			map.put("asset_id",result.get("asset_id"));
			map.put("alarm_type","电压低");

			//添加
			DbFactory.Open(DbFactory.FORM).insert("eam_alarm.addEamAlarm",map);
		}

		/**
		 * 距离信号警告
		 */
		if(50 < mqttBtMessage.getSignalIntensity()){
			HashMap map = new HashMap<String,Object>();
			map.put("alarm_num","WY" + System.currentTimeMillis());
			map.put("asset_id",result.get("asset_id"));
			map.put("alarm_type","位移");

			//添加
			DbFactory.Open(DbFactory.FORM).insert("eam_alarm.addEamAlarm",map);
		}
	}



}
