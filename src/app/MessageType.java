package app;

public enum MessageType {
	TOUCH,
	SIZE,
	JOIN_BROADCAST,
	JOIN_SEARCH,
	PRINT;
	
	public byte getFlag() {
		return (byte)this.ordinal();
	}
	
	public static MessageType convert(byte value) {
		return MessageType.values()[value];
	}
}