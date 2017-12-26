package com.humbels.mqtt;

public class MqttUtil {

	public static byte[] toMqttString(String s){
		if(s==null) return new byte[0];
		byte[] bytes = s.getBytes();
		byte[] ret = new byte[bytes.length + 2];
		assert(bytes.length <= 0xffff);
		ret[0] = (byte)(bytes.length >> 8);
		ret[1] = (byte)(bytes.length & 0xff);
		System.arraycopy(bytes, 0, ret, 2, bytes.length);
		
		return ret;	
	}

}
