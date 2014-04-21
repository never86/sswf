/**
 * 
 */
package ws.prova.mule.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ws.prova.kernel2.ProvaList;

/**
 * Used to detect infinite loops in workflows 
 * 
 * @version 0.1
 * 
 * @date 2014-04-21
 * 
 * @author Zhili Zhao
 */
public class InfiniteLoopDetector {

	protected static ConcurrentMap<String, Map> messageCounter = new ConcurrentHashMap<String, Map>();

	/**
	 * @param args
	 */
	public static void registerWf(String xid) {
		Map<String, Integer> msgMap = new HashMap<String, Integer>();
		if (!messageCounter.containsKey(xid))
			messageCounter.put(xid, msgMap);
	}

	public static void unregisterWf(String xid) {
		messageCounter.remove(xid);
	}

	public static boolean registerMsg(String xid, ProvaList msg) {
		if (messageCounter.containsKey(xid)) {
			Map msgMp = messageCounter.get(xid);
			if (msgMp.containsKey(msg.toString())) {
				int counter = (int) msgMp.get(msg.toString());
				counter++;
				if (counter > 10)
					return false;
				msgMp.remove(msg.toString());
				msgMp.put(msg.toString(), counter);
				return true;
			} else{
				msgMp.put(msg.toString(), 0);
			}
			return true;
		}
		return true;
	}

}
