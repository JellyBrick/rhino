package org.mozilla.rhino;

import java.util.Iterator;

public class Iterators {

  public static <E> void forEachRemaining(Iterator<E> iterator,  Consumer<? super E> action) {
    Objects.requireNonNull(action);
    while (iterator.hasNext())
      action.accept(iterator.next());
  }
}
