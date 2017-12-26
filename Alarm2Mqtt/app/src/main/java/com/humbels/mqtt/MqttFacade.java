package com.humbels.mqtt;

import java.io.IOException;

public class MqttFacade {
	public static void main(String[] args) {
		try {
			sendMessage("USERNAME", "PASSWORD", "SERVER", 1883, new PublishMsg("test","ciao boed!".getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConnectionRefusedException e) {
			e.printStackTrace();
		}
	}

	public static void sendMessage(String un, String pw, String server, int port, final PublishMsg msg) throws IOException, ConnectionRefusedException {
		MqttClient client = new MqttClient() {
			@Override
			public void onConnect(int i) throws IOException, ConnectionRefusedException {
				if(i == 0) {
					publish(msg);
					disconnect();
				} else {
					throw new ConnectionRefusedException(i);
				}
			}
		};
		client.setUnPw(un, pw);
		client.connect(server, port);
		// connect will loop until disconnect is called
		System.out.println("Im done!");

	}

}
