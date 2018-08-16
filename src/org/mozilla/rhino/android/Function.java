package org.mozilla.rhino.android;

public interface Function<T, R> {
    R apply(T t);
}
