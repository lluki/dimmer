package com.humbels.mqtt;


public class PublishMsg {
	// see http://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#msg-format
	private String topic = null; // must be set
	private byte[] payload;
	private FixedHeader fix;
	private int qosLevel = 0;
	
	public PublishMsg(String topic, byte[] payload) {
		assert(topic != null);
		this.topic = topic;
		this.payload = payload;
		fix = new FixedHeader();
		fix.setMessageType(FixedHeader.MT_PUBLISH);
	}
	
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public byte[] toByteArray(){
		assert(qosLevel == 0);
		// Variable header consists of topic string
		byte[] topicStr = MqttUtil.toMqttString(topic);
		fix.setRemainingLength(topicStr.length + payload.length);
		
		byte[] fixA = fix.toByteArray();
		byte[] res = new byte[fixA.length + topicStr.length + payload.length];
		System.arraycopy(fixA, 0, res, 0, fixA.length);
		System.arraycopy(topicStr, 0, res, fixA.length, topicStr.length);
		System.arraycopy(payload,0, res, topicStr.length + fixA.length, payload.length);
		return res;
	}
}