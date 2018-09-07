package org.mozilla.javascript;

final class CompatObjects {

  static boolean equals(Object a, Object b) {
    return (a == b) || (a != null && a.equals(b));
  }

  static boolean deepEquals(Object a, Object b) {
    if (a == b)
      return true;
    else if (a == null || b == null)
      return false;
    else
      return CompatArrays.deepEquals0(a, b);
  }

  static <T> T requireNonNull(T obj) {
    if (obj == null)
      throw new NullPointerException();
    return obj;
  }
}
