package ru.fly.client.ui.tree;

import ru.fly.shared.Getter;

/**
 * User: fil
 * Date: 22.04.15
 */
public interface TreeGetter<T> extends Getter<T> {

    boolean isFolder(T model);

}
