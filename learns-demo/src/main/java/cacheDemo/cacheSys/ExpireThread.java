package cacheDemo.cacheSys;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ExpireThread implements Runnable {
	@Override
	public void run() {
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(10);

				expireCache();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void expireCache() {

		for (String key : CacheGlobal.concurrentHashMap.keySet()) {
			MyCache myCache = CacheGlobal.concurrentHashMap.get(key);

			long timoutTime = System.currentTimeMillis() - myCache.getWriteTime();
			if (myCache.getExpireTime() > timoutTime) {
				continue;
			}
			CacheGlobal.concurrentHashMap.remove(key);
		}
	}
}
