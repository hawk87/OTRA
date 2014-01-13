package app.communication;

import java.net.InetAddress;

public class Message {

	private InetAddress address;
	private byte[] data;

	public Message(InetAddress address, byte[] data) {
		this.address = address;
		this.data = data;
	}
	
	public InetAddress getAddress()
	{
		return this.address;
	}
	
	public byte[] getData(){
		return this.data;
	}
}