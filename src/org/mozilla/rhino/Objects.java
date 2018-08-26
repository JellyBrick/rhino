package org.mozilla.rhino;

public final class Objects {

  public static boolean equals(Object a, Object b) {
    return (a == b) || (a != null && a.equals(b));
  }

  public static boolean deepEquals(Object a, Object b) {
    if (a == b)
      return true;
    else if (a == null || b == null)
      return false;
    else
      return Arrays.deepEquals0(a, b);
  }

  public static <T> T requireNonNull(T obj) {
    if (obj == null)
      throw new NullPointerException();
    return obj;
  }
}
