package ru.fly.client.ui.tree;

import ru.fly.shared.Getter;

import java.util.List;

/**
 * @author fil
 */
public abstract class TreeGetter<T> implements Getter<T> {

    public abstract boolean isFolder(T model);

    public abstract List<? extends T> getChildren(T model);

    public boolean isSelectable(T model) {
        return true;
    }

    public boolean hasChildren(T model) {
        return true;
    }

}
