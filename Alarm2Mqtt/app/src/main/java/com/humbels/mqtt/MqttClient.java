package com.humbels.mqtt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MqttClient {
	private Socket s;
	private OutputStream outputStream;
	private boolean keepRunning = true;
	private String username;
	private String password;

	public MqttClient() {
	
	}
	
	public void setUnPw(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public void connect(String host, int port) throws UnknownHostException, IOException, ConnectionRefusedException{
		System.out.println("Trying to connect to "+host+"...\n");

		s = new Socket(host, port);
		ConnectMsg connectMsg = new ConnectMsg("stupicl00");
		connectMsg.setUsername(username);
		connectMsg.setPassword(password);
		outputStream = s.getOutputStream();
		outputStream.write(connectMsg.toByteArray());
		
		
		System.out.println("Msg written...");
		outputStream.flush();
		System.out.println("Stream flushed written. Trying to read...");
		while(keepRunning){
			parseResponse(s.getInputStream());
		}
		s.close();
	}

	public void publish(PublishMsg publishMsg) throws IOException{
		outputStream.write(publishMsg.toByteArray());
	}
	
	public void publish(String topic, byte[] payload) throws IOException{
		publish(new PublishMsg(topic, payload));
	}
	
	public void onConnect(int i) throws IOException, ConnectionRefusedException{
		System.out.println("Connected, override me!");
	}

	public void disconnect() throws IOException {
		FixedHeader fix = new FixedHeader();
		fix.setMessageType(FixedHeader.MT_DISCONNECT);
		outputStream.write(fix.toByteArray());
		keepRunning = false;
	}

	/**
	 * Read one packet of off stream.
	 * @param inputStream
	 * @throws IOException 
	 * @throws ConnectionRefusedException 
	 */
	private void parseResponse(InputStream is) throws IOException, ConnectionRefusedException {
		
		FixedHeader fix = FixedHeader.parse(is);
		int toConsume = fix.getRemainingLength();
		
		switch(fix.getMessageType()){
		case FixedHeader.MT_CONNACK:
			is.read(); // drop one byte
			onConnect(is.read()); // second byte contains return code
			toConsume -= 2;
			break;
		default:
			System.out.println("Unhandled message type: " + fix.getMessageType());
			break;
		}
		
		while(toConsume-- > 0){
			if(is.read() == -1){
				throw new SocketException("Eof?");
			}
		}
		
	}
	
	
}
