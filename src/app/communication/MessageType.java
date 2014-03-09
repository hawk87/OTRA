package app.communication;

public enum MessageType {
	TOUCH,
	HEIGHT,
	JOIN_BROADCAST, //broadcast
	JOIN_RESPONSE,
	JOIN_SEARCH,
	BALANCE,
	SET_PARENT,
	SET_LEFT,
	SET_RIGHT,
	PRINT, 		//broadcast
	DISCONNECTED,  //broadcast
	DSCNN_RESPONSE,
	RECOVERY_FIND_MAX;
	
	public byte getFlag() {
		return (byte)this.ordinal();
	}
	
	public static MessageType convert(byte value) {
		return MessageType.values()[value];
	}
}