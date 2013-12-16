
public class IntegerWrapper {
	
	public static void main(String arg[]) {
		int s = Integer.parseInt(arg[0]);
		byte[] b = new byte[4];
		
		for(int i = 0; i < b.length; i++) {
			b[i] = (byte) s;
			System.out.println((byte) s);
			s = s >> 8;
		}
		
		int k = 0;
		int temp = 0;
		
		for(int j = 0; j < b.length; j++) {
			temp = (int) b[j];
			k = k + (temp << (8*j));
		}
		
		System.out.println(k);
		
		
//		String str = Integer.toBinaryString(s);
//		System.out.println(str);
		
//		byte[] a = str.getBytes();
		
//		for (int i = 0; i < a.length; i++) {
//			System.out.println(a[i]);
//		}
	}
}
