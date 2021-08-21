package il.ac.hit.services.objects;

import il.ac.hit.objects.Index;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This implementation supports multiple threads updating and getting {@link ConcurrentIndexData#parent} && {@link ConcurrentIndexData#distance}
 */
public class ConcurrentIndexData extends IndexData {
    private final ReadWriteLock parentAndDistanceLock = new ReentrantReadWriteLock();

    public ConcurrentIndexData(Index parent, Integer distance) {
        super(parent, distance);
    }

    @Override
    public Index getParent() {
        Lock lock = parentAndDistanceLock.readLock();
        lock.lock();
        try {
            return this.parent;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Integer getDistance() {
        Lock lock = parentAndDistanceLock.readLock();
        lock.lock();
        try {
            return this.distance;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setParentAndDistance(Index parent, int distance) {
        Lock lock = parentAndDistanceLock.writeLock();
        lock.lock();
        try {
            this.parent = parent;
            this.distance = distance;
        } finally {
            lock.unlock();
        }
    }
}