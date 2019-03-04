package org.mozilla.javascript.compat;

import java.util.Map;

public class CompatMaps {

  public static <K, V> V computeIfAbsent(
      Map<K, V> map,
      K key,
      CompatFunction<? super K, ? extends V> mappingFunction
  ) {
    CompatObjects.requireNonNull(mappingFunction);
    V v;
    if ((v = map.get(key)) == null) {
      V newValue;
      if ((newValue = mappingFunction.apply(key)) != null) {
        map.put(key, newValue);
        return newValue;
      }
    }

    return v;
  }
}
