package threadcommunication;

import java.util.concurrent.LinkedBlockingQueue;

public class RequestForSDKServiceBlockingQueue extends LinkedBlockingQueue<RequestForSDKServiceData>{
	
	private static RequestForSDKServiceBlockingQueue linkedBlockingQueueData = new RequestForSDKServiceBlockingQueue();
	
	public static RequestForSDKServiceBlockingQueue getLinkedBlockingQueueData() {
		return linkedBlockingQueueData;
	}
}
