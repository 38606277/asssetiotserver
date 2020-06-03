package root.mqtt.bean;

public class MQTTBtMessage {

	private String code; //编号
	private float signalIntensity;//信号强度
	private float electricity;//电量

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public float getSignalIntensity() {
		return signalIntensity;
	}

	public void setSignalIntensity(float signalIntensity) {
		this.signalIntensity = signalIntensity;
	}

	public float getElectricity() {
		return electricity;
	}

	public void setElectricity(float electricity) {
		this.electricity = electricity;
	}

	@Override
	public String toString() {
		return "MQTTBtMessage{" +
				"code='" + code + '\'' +
				", signalIntensity=" + signalIntensity +
				", electricity=" + electricity +
				'}';
	}
}
