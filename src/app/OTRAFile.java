package app;

public class OTRAFile {
	private int ID;
	private byte[] data;
	private String name;

	public OTRAFile(int ID, String name, byte[] data) {
		this.ID = ID;
		this.data = data;
		this.name = name;
	}

	public int getID() {
		return this.ID;
	}
	
	public String getName() {
		return this.name;
	}

	public byte[] getData() {
		return this.data;
	}
}