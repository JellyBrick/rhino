package org.mozilla.javascript;

interface CompatConsumer<T> {
  void accept(T t);
}
