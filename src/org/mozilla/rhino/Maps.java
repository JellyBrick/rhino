package org.mozilla.rhino;

import java.util.Map;

public class Maps {

  public static <K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
    V v = map.get(key);
    if (v == null) {
      v = map.put(key, value);
    }
    return v;
  }
}
