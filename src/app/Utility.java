package app;

public class Utility {
	
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
	
	public static int[] byteArrayToIntArray(byte[] data) {
		int n = data.length / 4;
		if(n * 4 != data.length) {
			System.out.println("Utility.byteArrayToIntArray: bad formatting");
			System.exit(0);
		}
		
		byte[] tmp = new byte[4];
		int[] vec = new int[n];
		for(int i=0; i < n; i++) {
			System.arraycopy(data, i*4, tmp, 0, 4);
			vec[i] = byteToInt(tmp);
		}
		return vec;
	}
	
	/**
	 * Utility to append an array to another.
	 * @param first
	 * @param second
	 * @return a (first.length + second.length) long array containing the two
	 * concatenated.
	 */
	public static byte[] appendArray(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);

		return result;
	}

}
