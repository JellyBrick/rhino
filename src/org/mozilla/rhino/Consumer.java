package org.mozilla.rhino;

public interface Consumer<T> {
  void accept(T t);
}
