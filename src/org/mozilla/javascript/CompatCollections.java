package org.mozilla.javascript;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

class CompatCollections {

  @SuppressWarnings("unchecked")
  static <T> Iterator<T> emptyIterator() {
    return (Iterator<T>) EmptyIterator.EMPTY_ITERATOR;
  }

  private static class EmptyIterator<E> implements Iterator<E> {
    static final EmptyIterator<Object> EMPTY_ITERATOR
        = new EmptyIterator<>();

    public boolean hasNext() { return false; }
    public E next() { throw new NoSuchElementException(); }
    public void remove() { throw new IllegalStateException(); }
    @Override
    public void forEachRemaining(Consumer<? super E> action) {
      CompatObjects.requireNonNull(action);
    }
  }
}
