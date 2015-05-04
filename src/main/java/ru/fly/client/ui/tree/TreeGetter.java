package ru.fly.client.ui.tree;

import ru.fly.shared.Getter;

/**
 * User: fil
 * Date: 22.04.15
 */
public abstract class TreeGetter<T> implements Getter<T> {

    public abstract boolean isFolder(T model);

    public boolean isSelectable(T model){
        return true;
    }

    public boolean hasChildren(T model){
        return true;
    }

}
