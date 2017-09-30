package threadcommunication;

import java.util.concurrent.LinkedBlockingQueue;

public class SynDirBlockingQueue extends LinkedBlockingQueue<String>{
	
	private static SynDirBlockingQueue linkedBlockingQueueData = new SynDirBlockingQueue();
	
	public static SynDirBlockingQueue getLinkedBlockingQueueData() {
		return linkedBlockingQueueData;
	}
}