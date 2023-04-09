package cacheDemo.cacheSys;

/**
 *
 */
public class CacheUtils {

	/**
	 * 放入缓存
	 *
	 * @param key    缓存key
	 * @param value  缓存值
	 * @param expire 过期时间(毫秒)
	 */
	public static void put(String key, Object value, long expire) {
		if (key.isEmpty()) return;

		if (CacheGlobal.concurrentHashMap.contains(key)) {
			MyCache myCache = CacheGlobal.concurrentHashMap.get(key);

			myCache.setValue(value);
			myCache.setHitCount(myCache.getHitCount() + 1);
			myCache.setWriteTime(System.currentTimeMillis());
			myCache.setLastTime(System.currentTimeMillis());
			myCache.setExpireTime(expire);
			return;
		}

		MyCache cache = new MyCache();
		cache.setKey(key);
		cache.setValue(value);
		cache.setExpireTime(expire);
		cache.setWriteTime(System.currentTimeMillis());
		cache.setLastTime(System.currentTimeMillis());
		cache.setHitCount(1);

		CacheGlobal.concurrentHashMap.put(key, cache);
	}

	/**
	 * 获取缓存
	 *
	 * @param key 缓存key
	 * @return 返回缓存值.
	 */
	public static Object get(String key) {
		if (key.isEmpty()) return null;
		if (CacheGlobal.concurrentHashMap.isEmpty()) return null;
		if (!CacheGlobal.concurrentHashMap.containsKey(key)) return null;

		MyCache myCache = CacheGlobal.concurrentHashMap.get(key);
		if (myCache == null) return null;

		// 惰性删除
		long timeout = System.currentTimeMillis() - myCache.getWriteTime();
		if (myCache.getExpireTime() <= timeout) {
			CacheGlobal.concurrentHashMap.remove(key);
		}
		myCache.setHitCount(myCache.getHitCount() + 1);
		myCache.setLastTime(System.currentTimeMillis());
		return myCache.getValue();
	}
}
