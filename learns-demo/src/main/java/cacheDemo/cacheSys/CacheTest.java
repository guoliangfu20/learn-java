package cacheDemo.cacheSys;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class CacheTest {
	public static void main(String[] args) throws InterruptedException {

		ExpireThread expireThread = new ExpireThread();

		Thread thread = new Thread(expireThread);
		thread.start();


		String key = "name";

		CacheUtils.put(key, "fuguoliang", 10000);

		boolean hasVal = true;

		while (hasVal) {
			Object nameObj = CacheUtils.get(key);
			if (nameObj != null) {
				String name = nameObj.toString();
				System.out.println(name);
			} else {
				hasVal = false;
				System.out.println("缓存清空了!");
			}
			TimeUnit.SECONDS.sleep(1);
		}
		System.out.println("game over!!");
	}
}
