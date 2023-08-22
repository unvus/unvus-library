package com.unvus.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LfuCache<K,V> implements Serializable {

    private long maxEntityQty;
    private long removeQtyWhenExceed;
    private boolean resetHitCountWhenExceed;

    private Map<K, HitObject<V>> cacheMap;

    public LfuCache(long maxEntityQty, long removeQtyWhenExceed, boolean resetHitCountWhenExceed) {
        this.maxEntityQty = maxEntityQty;
        this.removeQtyWhenExceed = removeQtyWhenExceed;
        this.resetHitCountWhenExceed = resetHitCountWhenExceed;
        this.cacheMap = new HashMap<>();
    }

    public synchronized V put(K key, V value) {
        if(cacheMap.size() >= maxEntityQty) {
            cacheMap.entrySet().stream()
                .sorted((e1,e2) -> (
                        Long.compare(e1.getValue().getHitCount(), e2.getValue().getHitCount()))
                    )
                .limit(removeQtyWhenExceed)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(k -> cacheMap.remove(k));


            if(resetHitCountWhenExceed) {
                cacheMap.values().forEach(HitObject::resetHitCount);
            }
        }
        log.debug("LfuCache::put::" + key);
        HitObject<V> hit = new HitObject<>(value);
        cacheMap.put(key, hit);
        return hit.getValue();
    }

    public V get(Object key) {
        HitObject<V> hit = cacheMap.get(key);
        hit.increase();
        return hit.getValue();
    }

    public boolean containsKey(Object key) {
        return cacheMap.containsKey(key);
    }

    private class HitObject<V> {
        private long hitCount;

        private V value;
        public HitObject(V value) {
            this.hitCount = 0;
            this.value = value;
        }

        public V getValue() {
            return value;
        }

        public long getHitCount() {
            return hitCount;
        }

        public void resetHitCount() {
            this.hitCount = 0;
        }

        public void increase() {
            this.hitCount++;
        }
    }

}
