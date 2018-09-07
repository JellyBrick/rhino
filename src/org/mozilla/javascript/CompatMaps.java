package org.mozilla.javascript;

import java.util.Map;

class CompatMaps {

  static <K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
    V v = map.get(key);
    if (v == null) {
      v = map.put(key, value);
    }
    return v;
  }
}
