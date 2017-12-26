package com.humbels.mqtt;

public class ConnectionRefusedException extends Exception {
	private static String errorCodes[] = {
		"Okay",
		"Connection Refused: unacceptable protocol version",
		"Connection Refused: identifier rejected",
		"Connection Refused: server unavailable",
		"Connection Refused: bad user name or password",
		"Connection Refused: not authorized"
	};

	public ConnectionRefusedException(int i) {
		super(errorCodes[i]);
	}

}
