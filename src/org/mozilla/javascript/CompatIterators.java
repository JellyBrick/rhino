package org.mozilla.javascript;

import java.util.Iterator;

class CompatIterators {

  static <E> void forEachRemaining(Iterator<E> iterator,  CompatConsumer<? super E> action) {
    CompatObjects.requireNonNull(action);
    while (iterator.hasNext())
      action.accept(iterator.next());
  }
}
