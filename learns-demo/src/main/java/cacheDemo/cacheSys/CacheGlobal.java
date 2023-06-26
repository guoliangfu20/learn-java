package cacheDemo.cacheSys;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache 全局类
 */
public class CacheGlobal {
	public static ConcurrentHashMap<String, MyCache> concurrentHashMap = new ConcurrentHashMap<>();

}
