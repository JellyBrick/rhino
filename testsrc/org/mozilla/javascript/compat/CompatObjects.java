package org.mozilla.javascript.compat;

public class CompatObjects {

  public static <T> T requireNonNull(T obj) {
    if (obj == null)
      throw new NullPointerException();
    return obj;
  }
}
