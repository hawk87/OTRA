package app;

public class IntegerUtility {
	
	/**
	 * Utility to convert a 32 bit integer to vector of 4 bytes.
	 * @return 4 bytes coding n
	 */
	public static byte[] intToByte(int n) {
		byte[] data = new byte[4];
		
		for(int i = 0; i < data.length; i++) {
			data[i] = (byte) n;
			n = n >> 8;
		}
		return data;
	}
	
	/**
	 * Utility to parse 4bytes array into an integer representation
	 * @param 4byte array containing the integer representation
	 * @return the integer value
	 */
	public static int byteToInt(byte[] data) {
		if(data.length != 4) {
			System.out.println("error parsing integer");
			System.exit(1);
		}
		int k = 0;
		int tmp;
		for(int j = 0; j < data.length; j++) {
			//need a mask to avoid sign propagation
			tmp = data[j] & 0x000000FF;
			k = k + (tmp << (8*j));
		}
		return k;
	}

}
