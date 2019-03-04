package org.mozilla.javascript.compat;

public interface CompatFunction<T, R> {
  R apply(T t);
}
