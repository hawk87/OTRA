package app;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * Define protocol messages.
 * This messages are essential for tree balancing operation.
 */
public class Messages {
	
	private BlockingQueue<SizeContent> queue;
	
	public Messages() {
		queue = new ArrayBlockingQueue<>(5);
	}
	
	public void size(Node p, int n) {
		//TODO
	}
	
	/*
	 * this call block
	 */
	public void touch(Node n) {
		//TODO
	}
	
	/*
	 * content of the SIZE message
	 */
	static class SizeContent {
		Node node;
		int size;
	}
	
	public SizeContent receiveSize() {
		SizeContent si = null;
		 try{
			 // this block until another thread fill queue
			 si = queue.take();
		 } catch (InterruptedException ie) {
			 System.out.println("receiveSend: interrupted exception");
			 System.exit(1);
		 }
		return si;
	}
	
	public void occurSize(Node n, int s) {
		SizeContent info = new SizeContent();
		info.node = n;
		info.size = s;
		try {
			queue.put(info);
		} catch (InterruptedException e) {
			System.out.println("occurSize: interrupted exception");
			System.exit(1);
		}
	}

}
