package app.communication;

public enum MessageType {
	TOUCH,
	HEIGHT,
	JOIN_BROADCAST,
	JOIN_SEARCH,
	BALANCE,
	SET_PARENT,
	SET_LEFT,
	SET_RIGHT,
	PRINT;
	
	public byte getFlag() {
		return (byte)this.ordinal();
	}
	
	public static MessageType convert(byte value) {
		return MessageType.values()[value];
	}
}