package org.mozilla.javascript;

import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.mozilla.rhino.BuildConfig;

public class ThreadSafeSlotMapContainer_jdk15 extends SlotMapContainer {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    ThreadSafeSlotMapContainer_jdk15(int initialSize) {
        super(initialSize);
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return map.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int dirtySize() {
        if (BuildConfig.DEBUG && lock.isWriteLocked()) {
            throw new AssertionError();
        }
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return map.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ScriptableObject.Slot get(Object key, int index, ScriptableObject.SlotAccess accessType) {
        lock.writeLock().lock();
        try {
            if (accessType != ScriptableObject.SlotAccess.QUERY) {
                checkMapSize();
            }
            return map.get(key, index, accessType);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public ScriptableObject.Slot query(Object key, int index) {
        lock.readLock().lock();
        try {
            return map.query(key, index);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void addSlot(ScriptableObject.Slot newSlot) {
        lock.writeLock().lock();
        try {
            checkMapSize();
            map.addSlot(newSlot);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(Object key, int index) {
        lock.writeLock().lock();
        try {
            map.remove(key, index);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long readLock() {
        lock.readLock().lock();
        return 0;
    }

    @Override
    public void unlockRead(long stamp) {
        lock.readLock().unlock();
    }

    @Override
    public Iterator<ScriptableObject.Slot> iterator() {
        if (BuildConfig.DEBUG && lock.isWriteLocked()) {
            throw new AssertionError();
        }
        return map.iterator();
    }

    protected void checkMapSize() {
        if (BuildConfig.DEBUG && !lock.isWriteLocked()) {
            throw new AssertionError();
        }
        super.checkMapSize();
    }
}
