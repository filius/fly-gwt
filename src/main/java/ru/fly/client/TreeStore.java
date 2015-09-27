package ru.fly.client;

import ru.fly.client.event.UpdateEvent;
import ru.fly.client.ui.EventBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: fil
 * Date: 21.04.15
 */
public class TreeStore<T> extends EventBase {

    private TreeStoreItem<T> root = new TreeStoreItem<>(null,null);
    private Map<T, TreeStoreItem<T>> links = new HashMap<>();

    public boolean isEmpty(){
        return root.getChildren().isEmpty();
    }

    public void clear(){
        root = new TreeStoreItem<>(null, null);
        links.clear();
        fireEvent(new UpdateEvent());
    }

    public int size(T parent){
        return getItem(parent).getChildren().size();
    }

    public void add(T parent, T model){
        add(parent, model, false);
    }

    public void add(T parent, T model, boolean expanded){
        if(model == null){
            throw new IllegalArgumentException("Model cant be NULL!");
        }
        TreeStoreItem<T> item = new TreeStoreItem<>(parent, model, expanded);
        getItem(parent).getChildren().add(item);
        links.put(model, item);
        fireEvent(new UpdateEvent());
    }

    public void addAll(T parent, Collection<? extends T> models){
        TreeStoreItem<T> parentItem = getItem(parent);
        for(T model : models){
            if(model == null){
                throw new IllegalArgumentException("Model cant be NULL!");
            }
            TreeStoreItem<T> item = new TreeStoreItem<>(parent, model);
            parentItem.getChildren().add(item);
            links.put(model, item);
        }
        fireEvent(new UpdateEvent());
    }

    public boolean contains(T model){
        for(T m : links.keySet()){
            if(m.equals(model)){
                return true;
            }
        }
        return false;
    }

    public boolean isExpanded(T model){
        return getItem(model).isExpanded();
    }

    public TreeStoreItem<T> getItem(T model){
        if(model == null){
            return root;
        }else{
            for(T m : links.keySet()){
                if(m.equals(model)){
                    return links.get(m);
                }
            }
            return null;
        }
    }

    public List<TreeStoreItem<T>> getItemChildren(T parent){
        return getItem(parent).getChildren();
    }

    public List<T> getChildren(T parent){
        return getItem(parent).getModelChildren();
    }

    public void addUpdateHandler(UpdateEvent.UpdateHandler lnr){
        addHandler(lnr, UpdateEvent.getType());
    }

    // ------------------ privates -------------------

}
