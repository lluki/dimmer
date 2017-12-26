package com.humbels.mqtt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class ConnectMsg {
	// see http://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#msg-format
	String id = null; // must be set
	String willTopic = null; // can be set
	String willMsg = null; // can be set
	String username = null; // can be set
	String password = null; // can be set
	
	FixedHeader fix;
	ConnectVarHeader var;
	
	public ConnectMsg(String id) {
		assert(id != null);
		this.id = id;
		fix = new FixedHeader();
		fix.setMessageType(FixedHeader.MT_CONNECT);
		var = new ConnectVarHeader();
	}
	
	public void setWillMsg(String willMsg) {
		this.willMsg = willMsg;
	}
	public void setWillTopic(String willTopic) {
		this.willTopic = willTopic;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public byte[] toByteArray() throws IOException{
		if(username != null) var.setHasUsername();
		if(password != null) var.setHasPassword();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] varA = var.toByteArray();
		byte[] idBytes = MqttUtil.toMqttString(id);
		
		byte[] unBytes = MqttUtil.toMqttString(username);
		byte[] pwBytes = MqttUtil.toMqttString(password);
		
		fix.setRemainingLength(varA.length + idBytes.length + unBytes.length + pwBytes.length);
		
		baos.write(fix.toByteArray());
		baos.write(varA);
		baos.write(idBytes);
		baos.write(unBytes);
		baos.write(pwBytes);
		return baos.toByteArray();
	}
}