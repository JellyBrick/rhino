package org.mozilla.rhino;

public interface Function<T, R> {
  R apply(T t);
}
