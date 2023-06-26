package cacheDemo;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 *
 */
public class EhCacheMananger {

	public static void main(String[] args) {

		// 创建缓存管理器
		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();

		// 初始化 EhCache
		cacheManager.init();

		Cache<String, String> mycache = cacheManager.createCache("MYCACHE",
				CacheConfigurationBuilder.newCacheConfigurationBuilder(
						String.class, String.class,
						ResourcePoolsBuilder.heap(10)));  // 设置缓存的最大容量

		mycache.put("mykey", "myValue");

		String mykey = mycache.get("mykey");
		System.out.println(mykey);

		// 关闭缓存
		cacheManager.close();
	}
}
