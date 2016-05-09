package ru.fly.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Collection;

/**
 * @author fil.
 */
public interface TreeLoader<T> {

    void load(T parent, AsyncCallback<Collection<T>> cback);

}
