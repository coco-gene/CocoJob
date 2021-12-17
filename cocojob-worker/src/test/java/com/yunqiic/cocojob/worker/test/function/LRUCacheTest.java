package com.yunqiic.cocojob.worker.test.function;

import com.yunqiic.cocojob.worker.common.utils.LRUCache;
import org.junit.jupiter.api.Test;

/**
 * LRU cache test
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class LRUCacheTest {

    @Test
    public void testCache() {
        LRUCache<Long, String> cache = new LRUCache<>(10);
        for (long i = 0; i < 100; i++) {
            cache.put(i, "STR:" + i);
        }
        cache.forEach((x, y) -> System.out.println("key:" + x));
    }

}
