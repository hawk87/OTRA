import java.net.InetAddress;
import java.util.ArrayList;

public class Cache {

	// Fields
	private static ArrayList<Row> table = new ArrayList<Row>();

	// Methods
	public static boolean isThere(InetAddress ip, byte seq) {
		boolean sw = false;
		Row r = new Row(ip, seq);
		int index = searchByIP(ip);

		if (index == -1) {
			if (table.size() >= 10) {
				table.remove(0);
			}
			table.add(r);
		}
		else {
			Row d = table.remove(index);
			if (d.getSeq() == seq) {
				sw = true;
			}
			table.add(r);
		}
		return sw;
	}

	private static int searchByIP(InetAddress ip) {
		int index = -1;

		for (int i = 0; i < table.size(); i++) {
			if (table.get(i).getAddr().equals(ip)) {
				index = i;
				break;
			}
		}
		return index;
	}

	public static void print() {
		System.out.println("InetAddress, SeqNumb");
		for(int i = 0; i < table.size(); i++) {
			System.out.println(" --- "+table.get(i).getAddr()+" : "+table.get(i).getSeq());
		}
	}

	// --- PRIVATE CLASS ---
	private static class Row {

		private InetAddress addr;
		private byte seq;

		public Row(InetAddress a, byte s) {
			setAddr(a);
			setSeq(s);
		}

		public byte getSeq() {
			return seq;
		}

		public void setSeq(byte seq) {
			this.seq = seq;
		}

		public InetAddress getAddr() {
			return addr;
		}

		public void setAddr(InetAddress addr) {
			this.addr = addr;
		}
	}

}
