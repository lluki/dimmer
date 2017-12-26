package com.humbels.mqtt;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

public class FixedHeader {
	public static final int MT_CONNECT = 1;                                                                                                             
	public static final int MT_CONNACK = 2;                                                                                                             
	public static final int MT_PUBLISH = 3;                                                                                                             
	public static final int MT_PUBACK  = 4;                                                                                                             
	public static final int MT_PUBREC  = 5;                                                                                                             
	public static final int MT_PUBREL  = 6;                                                                                                             
	public static final int MT_PUBCOMP = 7;                                                                                                             
	public static final int MT_SUBSCRIBE   = 8;                                                                                                         
	public static final int MT_SUBACK  = 9;                                                                                                             
	public static final int MT_UNSUBSCRIBE = 12;                                                                                                        
	public static final int MT_UNSUBACK    = 13;                                                                                                        
	public static final int MT_PINGREQ = 14;                                                                                                            
	public static final int MT_PINGRESP    = 15;                                                                                                        
	public static final int MT_DISCONNECT  = 16;
	
	public static final int FLAG_DISCONNECT  = 16;
	
	private int messageType;
	private int dupFlag;
	private int qosLevel;
	private int remainingLength;
	private int retain;
	
	public int getDupFlag() {
		return dupFlag;
	}
	public void setDupFlag(int dupFlag) {
		this.dupFlag = dupFlag;
	}
	public int getMessageType() {
		return messageType;
	}
	public int getRetain() {
		return retain;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public int getQosLevel() {
		return qosLevel;
	}
	public void setQosLevel(int qosLevel) {
		this.qosLevel = qosLevel;
	}
	public int getRemainingLength() {
		return remainingLength;
	}
	public void setRemainingLength(int remainingLength) {
		this.remainingLength = remainingLength;
	}
	public void setRetain(int retain) {
		this.retain = retain;
	}
	
	public byte[] toByteArray(){
		byte ret[] = new byte[2];
		ret[0] = (byte)((messageType << 4) | (dupFlag << 3 & 1) | (qosLevel << 1 & 3) | (retain & 1));
		assert(remainingLength < 128); // Must do multi byte encoding otherwise
		ret[1] = (byte)remainingLength;
		return ret;
	}
	
	public static FixedHeader parse(InputStream is) throws IOException {
		int a = is.read();
		if(a == -1) {
			throw new SocketException("Eof?");
		}
		FixedHeader ret = new FixedHeader();
	    ret.setMessageType(a >> 4);
	    ret.setDupFlag((a >> 3) & 1);
		ret.setQosLevel((a>>2) & 3);
		ret.setRetain(a & 1);
		
		int b = is.read();
		if(b == -1) {
			throw new SocketException("Eof?");
		}
		ret.setRemainingLength(b);
		
		return ret;
	}
}