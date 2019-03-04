package org.mozilla.javascript.compat;

import java.util.NoSuchElementException;

public class CompatOptional<T> {

  private final T value;

  private CompatOptional(T value) {
    this.value = CompatObjects.requireNonNull(value);
  }

  public static <T> CompatOptional<T> of(T value) {
    return new CompatOptional<>(value);
  }

  public T get() {
    if (value == null) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }
}
