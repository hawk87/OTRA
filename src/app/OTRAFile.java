package app;

public class OTRAFile {
	private int ID;
	private byte[] data;

	public OTRAFile(int ID, byte[] data) {
		this.ID = ID;
		this.data = data;
	}

	public int getID() {
		return this.ID;
	}

	public byte[] getData() {
		return this.data;
	}
}