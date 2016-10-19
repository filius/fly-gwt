package ru.fly.shared;

/**
 * resolve key for identify object
 *
 * @author fil.
 */
public interface KeyResolver<T> {

    /**
     * return object key
     *
     * @param object - target object
     * @return - key
     */
    Object get(T object);

}
