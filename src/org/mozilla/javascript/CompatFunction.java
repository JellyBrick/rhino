package org.mozilla.javascript;

interface CompatFunction<T, R> {
  R apply(T t);
}
