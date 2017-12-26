package com.humbels.mqtt;

public class ConnectVarHeader {
	int length = 12;
	byte connectFlags = 0;
	
	public static final int USERNAME_F = 7;
	public static final int PW_F = 6;
	public static final int WILLRETAIN_F = 5;
	public static final int WILL_QOS_F = 3; //2 bit
	public static final int WILL_F = 2; 
	public static final int CLEAN_F = 1;
	// 0 bit reserved
	
	public void setConnectFlags(byte connectFlags) {
		this.connectFlags = connectFlags;
	}
	
	public byte getConnectFlags() {
		return connectFlags;
	}
	
	public void setHasUsername(){
		connectFlags |= (byte)(1<<USERNAME_F);
	}
	
	public void setHasPassword(){
		connectFlags |= (byte)(1<<PW_F);
	}
	
	public byte[] toByteArray(){
		byte ret[] = new byte[12];
		int i=0;
		ret[i++] =  0;
		ret[i++] = 6; // protocol string len
		ret[i++] = 'M';
		ret[i++] = 'Q';
		ret[i++] = 'I';
		ret[i++] = 's';
		ret[i++] = 'd';
		ret[i++] = 'p';
		
		ret[i++] = 3;
		ret[i++] = connectFlags; // set bare minimum
		 
		// keepalive
		ret[i++] = 0;
		ret[i++] = 10;
		
		assert(i==length);
		
		return ret;
	}
	
}