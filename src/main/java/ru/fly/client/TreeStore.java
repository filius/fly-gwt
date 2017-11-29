package ru.fly.client;

import com.google.gwt.event.shared.HandlerRegistration;
import ru.fly.client.event.UpdateEvent;
import ru.fly.client.event.UpdateEvent.HasUpdateHandler;
import ru.fly.client.log.Log;
import ru.fly.client.ui.EventBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fil
 */
public class TreeStore<T> extends EventBase implements HasUpdateHandler {

    private TreeStoreItem<T> root = new TreeStoreItem<>(null, null);
    private Map<T, TreeStoreItem<T>> links = new HashMap<>();

    @Override
    public HandlerRegistration addUpdateHandler(UpdateEvent.UpdateHandler lnr) {
        return addHandler(lnr, UpdateEvent.getType());
    }

    public void fireUpdateEvent() {
        fireEvent(new UpdateEvent());
    }

    public boolean isEmpty() {
        return root.getChildren().isEmpty();
    }

    public void clear() {
        root = new TreeStoreItem<>(null, null);
        links.clear();
        fireEvent(new UpdateEvent());
    }

    public int size(T parent) {
        return getItem(parent).getChildren().size();
    }

    public void add(T parent, T model) {
        add(parent, model, false);
    }

    public void add(T parent, T model, boolean expanded) {
        add(parent, model, expanded, true);
    }

    public void add(T parent, T model, boolean expanded, boolean fireEvent) {
        if (model == null) {
            throw new IllegalArgumentException("Model cant be NULL!");
        }
        TreeStoreItem<T> parentItem = getItem(parent);
        if (parentItem == null) {
            Log.error("Parent item not in store, " + parent);
            throw new IllegalStateException("Parent item not in store.");
        }
        TreeStoreItem<T> item = new TreeStoreItem<>(parent, model, expanded);
        parentItem.getChildren().add(item);
        links.put(model, item);
        if (fireEvent) {
            fireEvent(new UpdateEvent());
        }
    }

    public void addAll(T parent, Collection<? extends T> models) {
        addAll(parent, models, true);
    }

    public void addAll(T parent, Collection<? extends T> models, boolean fireEvent) {
        TreeStoreItem<T> parentItem = getItem(parent);
        for (T model : models) {
            if (model == null) {
                throw new IllegalArgumentException("Model cant be NULL!");
            }
            TreeStoreItem<T> item = new TreeStoreItem<>(parent, model);
            parentItem.getChildren().add(item);
            links.put(model, item);
        }
        if (fireEvent) {
            fireEvent(new UpdateEvent());
        }
    }

    public boolean contains(T model) {
        for (T m : links.keySet()) {
            if (m.equals(model)) {
                return true;
            }
        }
        return false;
    }

    public boolean isExpanded(T model) {
        return getItem(model).isExpanded();
    }

    public TreeStoreItem<T> getItem(T model) {
        if (model == null) {
            return root;
        } else {
            for (T m : links.keySet()) {
                if (m.equals(model)) {
                    return links.get(m);
                }
            }
            return null;
        }
    }

    public List<TreeStoreItem<T>> getItemChildren(T parent) {
        return getItem(parent).getChildren();
    }

    public List<T> getChildren(T parent) {
        return getItem(parent).getModelChildren();
    }

}
