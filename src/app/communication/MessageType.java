package app.communication;

public enum MessageType {
	TOUCH,
	SIZE,
	JOIN_BROADCAST,
	JOIN_SEARCH,
	BALANCE,
	PRINT;
	
	public byte getFlag() {
		return (byte)this.ordinal();
	}
	
	public static MessageType convert(byte value) {
		return MessageType.values()[value];
	}
}